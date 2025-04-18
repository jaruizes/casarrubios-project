import logging
import os
import sys
import threading
import time
import uuid
from pathlib import Path

from confluent_kafka import Producer
from minio import Minio
from sqlalchemy import text

import pytest
from testcontainers.core.container import DockerContainer
from testcontainers.core.waiting_utils import wait_for_logs
from testcontainers.postgres import PostgresContainer
from testcontainers.kafka import KafkaContainer

from src.infrastructure.core.config import load_config
from src.main import main, startup
from src.infrastructure.db.sqlalchemy_connection import SQLAlchemyConnection

logging.basicConfig(
    format="%(asctime)s - %(levelname)s - %(message)s",
    level=logging.DEBUG,
    handlers=[logging.StreamHandler(stream=sys.stdout)],
)

logger = logging.getLogger(__name__)


@pytest.fixture(scope="module")
def postgres_container(request):
    script = Path(__file__).parent.parent / "postgresql" / "inventory.sql"
    postgres = (PostgresContainer("postgres:14")
                .with_env("POSTGRES_PORT", "5432")
                .with_env("POSTGRES_USER", "test")
                .with_env("POSTGRES_PASSWORD", "test")
                .with_env("POSTGRES_DB", "test")
                .with_volume_mapping(host=str(script), container=f"/docker-entrypoint-initdb.d/{script.name}"))

    postgres.start()

    logger.info(f"PostgreSQL container started: {postgres.get_connection_url()}")
    yield postgres
    postgres.stop()
    logger.info("PostgreSQL container stopped")

@pytest.fixture(scope="module")
def kafka_container(request):
    kafka = KafkaContainer("confluentinc/cp-kafka:latest")
    kafka.start()
    bootstrap_servers = kafka.get_bootstrap_server()
    
    # Esperar a que Kafka esté listo
    from confluent_kafka.admin import AdminClient
    retries = 10
    while retries > 0:
        try:
            admin = AdminClient({'bootstrap.servers': bootstrap_servers})
            cluster_metadata = admin.list_topics(timeout=10)
            if cluster_metadata:
                break
        except Exception:
            retries -= 1
            time.sleep(2)
    
    if retries == 0:
        raise Exception("Kafka no está disponible después de varios intentos")
        
    logger.info(f"Kafka container started: {bootstrap_servers}")

    def remove_container():
        kafka.stop()
    # request.addfinalizer(remove_container)

    yield bootstrap_servers
    kafka.stop()
    logger.info("Kafka container stopped")

@pytest.fixture(scope="module")
def minio_container():
    class MinioContainer(DockerContainer):
        def __init__(self, image="quay.io/minio/minio:RELEASE.2025-02-18T16-25-55Z", port=9000):
            super(MinioContainer, self).__init__(image)
            self.with_exposed_ports(port, 9001)
            self.with_env("MINIO_ROOT_USER", "minioadmin")
            self.with_env("MINIO_ROOT_PASSWORD", "minioadmin")
            self.with_command("server /data --console-address \":9001\"")
            self.port = port
        
        def get_connection_url(self):
            host = self.get_container_host_ip()
            port = self.get_exposed_port(self.port)
            return f"{host}:{port}"
    
    minio = MinioContainer()
    minio.start()
    wait_for_logs(minio, "MinIO Object Storage Server")
    yield minio
    minio.stop()

@pytest.fixture(scope="module")
def minio_setup(minio_container):
    endpoint = f"localhost:{minio_container.get_exposed_port(9000)}"
    minio = Minio(
            endpoint=endpoint,
            access_key="minioadmin",
            secret_key="minioadmin",
            secure=False)

    minio.make_bucket("test")
    yield minio

@pytest.fixture(scope="module")
def setup_environment_variables(postgres_container, kafka_container, minio_container):
    import os
    os.environ["DB_HOST"] = postgres_container.get_container_host_ip()
    os.environ["DB_PORT"] = str(postgres_container.get_exposed_port(5432))
    os.environ["DB_USER"] = "test"
    os.environ["DB_PASSWORD"] = "test"
    os.environ["DB_NAME"] = "test"
    os.environ["KAFKA_BOOTSTRAP_SERVERS"] = kafka_container
    os.environ["KAFKA_INPUT_TOPIC"] = "recruiters.applications.scored"
    os.environ["MINIO_URL"] = f"localhost:{minio_container.get_exposed_port(9000)}"
    os.environ["MINIO_ACCESS_NAME"] = "minioadmin"
    os.environ["MINIO_ACCESS_SECRET"] = "minioadmin"
    os.environ["MINIO_BUCKET_NAME"] = "test"

    yield

@pytest.fixture(scope="module")
def setup_topics(kafka_container, setup_environment_variables):
    from confluent_kafka.admin import (AdminClient, NewTopic)

    admin = AdminClient({'bootstrap.servers': kafka_container} )
    applications_scored_topic = os.getenv("KAFKA_INPUT_TOPIC")

    new_applications_scored_topic = NewTopic(applications_scored_topic, num_partitions=1, replication_factor=1)

    result_dict = admin.create_topics([new_applications_scored_topic])
    for topic, future in result_dict.items():
        try:
            future.result()
            print("Topic {} created".format(topic))
        except Exception as e:
            print("Failed to create topic {}: {}".format(topic, e))
            raise e

    yield

@pytest.fixture(scope="module")
def setup_config(setup_environment_variables):
    config = load_config()
    yield config

@pytest.fixture(scope="module")
def db_session(setup_config):
    db_connection = SQLAlchemyConnection(
        host="localhost",
        port=setup_config.port,
        user="test",
        password="test",
        database="test"
    )
    session_factory = db_connection.connect()
    db_session = session_factory()
    yield db_session
    db_connection.disconnect()

@pytest.fixture(scope="module")
def setup_producer(setup_config):
    conf = {'bootstrap.servers': setup_config.bootstrap_servers}
    producer = Producer(conf)

    yield producer

@pytest.fixture(scope="module")
def event_processor(db_session):
    from tests.test_utils import TestEventProcessor
    return TestEventProcessor(db_session)

@pytest.fixture(scope="module")
def wait_for_process_event(db_session):

    import asyncio

    async def _wait_for_result(application_id, timeout=300):
        try:
            start_time = time.time()
            while time.time() - start_time < timeout:
                # Hacer commit para asegurarnos de que estamos viendo los datos más recientes
                db_session.commit()
                
                result_analysis = db_session.execute(
                    text('SELECT * FROM recruiters.candidate_analysis WHERE candidate_id = (SELECT candidate_id FROM recruiters.candidate_applications WHERE id = :application_id)'),
                    {"application_id": application_id}
                )

                result_scoring = db_session.execute(
                    text('SELECT * FROM recruiters.application_scoring WHERE application_id = :application_id'),
                    {"application_id": application_id}
                )
                
                rows_analysis = result_analysis.fetchall()
                rows_scoring = result_scoring.fetchall()
                if rows_analysis and len(rows_analysis) > 0 and rows_scoring and len(rows_scoring) > 0:
                    return rows_analysis

                # Esperar un poco antes de volver a consultar
                await asyncio.sleep(5)
            
            # Si llegamos aquí, significa que se agotó el tiempo de espera
            logging.warning(f"Timeout waiting for application_id {application_id} to be processed")
            return None

        except Exception as e:
            logging.error(f"Error in wait_for_process_event: {str(e)}")
            raise e

    return _wait_for_result


@pytest.fixture(scope="module")
def setup_e2e(setup_topics):
    import socket
    
    def find_free_port():
        with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
            s.bind(('', 0))
            s.listen(1)
            port = s.getsockname()[1]
        return port

    # Configurar un puerto libre para uvicorn
    os.environ["PORT"] = str(find_free_port())
    
    app_thread = threading.Thread(target=startup, daemon=True)
    app_thread.start()

    time.sleep(5)

    yield
