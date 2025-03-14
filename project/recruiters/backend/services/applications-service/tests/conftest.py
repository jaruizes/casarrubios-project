import logging
import os
import sys
import threading
import time
import uuid
from pathlib import Path

from confluent_kafka import Producer
from sqlalchemy import text

import pytest
from src.main import main
from testcontainers.postgres import PostgresContainer
from testcontainers.kafka import KafkaContainer

from src.infrastructure.core.config import load_config
from src.infrastructure.db.sqlalchemy_connection import SQLAlchemyConnection

logging.basicConfig(
    format="%(asctime)s - %(levelname)s - %(message)s",
    level=logging.DEBUG,
    handlers=[logging.StreamHandler(stream=sys.stdout)],
)

logger = logging.getLogger(__name__)


@pytest.fixture(scope="module")
def postgres_container(request):
    script = Path(__file__).parent / "sql" / "init.sql"
    postgres = (PostgresContainer("postgres:14")
                .with_env("POSTGRES_PORT", "5432")
                .with_env("POSTGRES_USER", "test")
                .with_env("POSTGRES_PASSWORD", "test")
                .with_env("POSTGRES_DB", "test")
                .with_volume_mapping(host=str(script), container=f"/docker-entrypoint-initdb.d/{script.name}"))

    postgres.start()

    def remove_container():
        postgres.stop()
    # request.addfinalizer(remove_container)

    logger.info(f"PostgreSQL container started: {postgres.get_connection_url()}")
    yield postgres
    postgres.stop()
    logger.info("PostgreSQL container stopped")

@pytest.fixture(scope="module")
def kafka_container(request):
    kafka = KafkaContainer("confluentinc/cp-kafka:latest")
    kafka.start()
    bootstrap_servers = kafka.get_bootstrap_server()
    logger.info(f"Kafka container started: {bootstrap_servers}")

    def remove_container():
        kafka.stop()
    # request.addfinalizer(remove_container)

    yield bootstrap_servers
    kafka.stop()
    logger.info("Kafka container stopped")


@pytest.fixture(scope="module")
def setup_environment_variables(postgres_container, kafka_container):
    import os
    os.environ["DB_HOST"] = postgres_container.get_container_host_ip()
    os.environ["DB_PORT"] = str(postgres_container.get_exposed_port(5432))
    os.environ["DB_USER"] = "test"
    os.environ["DB_PASSWORD"] = "test"
    os.environ["DB_DATABASE"] = "test"
    os.environ["KAFKA_BOOTSTRAP_SERVERS"] = kafka_container
    os.environ["KAFKA_INPUT_TOPIC"] = "recruiters.applications.scored"

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
def wait_for_process_event(db_session):

    async def _wait_for_result(application_id, timeout=300):
        try:

            start_time = time.time()
            while time.time() - start_time < timeout:
                result = db_session.execute(
                    text('SELECT * FROM recruiters.resume_analysis WHERE application_id = :application_id'),
                    {"application_id": application_id}
                )
                if result is not None:
                    rows = result.fetchall()
                    if rows and len(rows) > 0:
                        return rows

                time.sleep(2)

        except Exception as e:
            raise e

    return _wait_for_result


@pytest.fixture(scope="module")
def setup_e2e(setup_topics):
    app_thread = threading.Thread(target=main, daemon=True)
    app_thread.start()

    time.sleep(5)

    yield
