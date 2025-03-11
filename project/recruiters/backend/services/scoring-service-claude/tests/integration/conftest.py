# tests/conftest.py
import asyncio
from pathlib import Path

import pytest
import json
import logging
import time
from testcontainers.postgres import PostgresContainer
from testcontainers.kafka import KafkaContainer
from aiokafka import AIOKafkaProducer, AIOKafkaConsumer

from sqlalchemy import create_engine, text
from sqlalchemy.orm import sessionmaker
from src.application.adapters.db.models import Base
from src.application.adapters.db.sqlalchemy_repository import SQLAlchemyPositionRepository
from src.application.adapters.messaging.kafka_producer import KafkaProducer
from src.application.adapters.messaging.kafka_consumer import KafkaConsumer
from src.domain.services.scoring_service import ScoringService
from src.application.usecases.process_application import ProcessApplicationUseCase

# Configuración de logging para las pruebas
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)


@pytest.fixture(scope="session")
def event_loop():
    """Crea un nuevo event loop para los tests"""
    loop = asyncio.get_event_loop_policy().new_event_loop()
    yield loop
    loop.close()


@pytest.fixture(scope="session")
def postgres_container():
    """Inicia un contenedor de PostgreSQL para pruebas"""
    script = Path(__file__).parent / "sql" / "init.sql"
    postgres = (PostgresContainer("postgres:14")
                .with_volume_mapping(host=str(script), container=f"/docker-entrypoint-initdb.d/{script.name}"))

    postgres.start()
    logger.info(f"PostgreSQL container started: {postgres.get_connection_url()}")
    yield postgres
    postgres.stop()
    logger.info("PostgreSQL container stopped")


@pytest.fixture(scope="session")
def kafka_container():
    """Inicia un contenedor de Kafka para pruebas"""
    kafka = KafkaContainer("confluentinc/cp-kafka:latest")
    kafka.start()
    bootstrap_servers = kafka.get_bootstrap_server()
    logger.info(f"Kafka container started: {bootstrap_servers}")
    yield bootstrap_servers
    kafka.stop()
    logger.info("Kafka container stopped")


@pytest.fixture(scope="session")
def db_engine(postgres_container):
    """Crea un engine SQLAlchemy para las pruebas"""
    connection_url = postgres_container.get_connection_url()
    engine = create_engine(connection_url)

    yield engine

@pytest.fixture(scope="session")
def db_session_factory(db_engine):
    """Crea una factory de sesiones para las pruebas"""
    return sessionmaker(bind=db_engine, expire_on_commit=False)


@pytest.fixture(scope="module")
def db_session(db_session_factory):
    """Crea una sesión de base de datos para cada test"""
    session = db_session_factory()
    yield session
    session.close()


@pytest.fixture(scope="module")
def position_repository(db_session_factory):
    """Crea un repositorio de posiciones para pruebas"""
    return SQLAlchemyPositionRepository(db_session_factory)


@pytest.fixture(scope="module")
def kafka_producer(kafka_container):
    """Crea un productor de Kafka para pruebas"""
    producer = KafkaProducer(kafka_container)
    producer.initialize()
    yield producer
    producer.close()


@pytest.fixture(scope="module")
async def kafka_admin_client(kafka_container):
    """Crea tópicos necesarios para las pruebas"""
    from aiokafka.admin import AIOKafkaAdminClient
    from aiokafka.admin.new_topic import NewTopic

    admin_client = AIOKafkaAdminClient(bootstrap_servers=kafka_container)
    await admin_client.start()

    # Crear tópicos de prueba
    topics = [
        NewTopic(name="application-analysed-events", num_partitions=1, replication_factor=1),
        NewTopic(name="application-scored-events", num_partitions=1, replication_factor=1)
    ]

    try:
        await admin_client.create_topics(topics)
        logger.info("Created test Kafka topics")
    except Exception as e:
        logger.warning(f"Topics may already exist: {str(e)}")

    yield admin_client

    await admin_client.close()


@pytest.fixture(scope="module")
def scoring_service():
    """Crea un servicio de puntuación para pruebas"""
    return ScoringService()


@pytest.fixture(scope="module")
def process_application_use_case(position_repository, kafka_producer, scoring_service):
    use_case = ProcessApplicationUseCase(
        position_repository=position_repository,
        event_producer=kafka_producer,
        scoring_service=scoring_service
    )
    return use_case


@pytest.fixture(scope="module")
def wait_for_result_in_kafka(kafka_container):
    """Helper para esperar resultados en Kafka"""

    async def _wait_for_result(topic, timeout=10):
        consumer = AIOKafkaConsumer(
            topic,
            bootstrap_servers=kafka_container,
            auto_offset_reset='earliest',
            group_id='test-group',
            value_deserializer=lambda m: json.loads(m.decode('utf-8'))
        )

        await consumer.start()

        start_time = time.time()
        result = None

        try:
            while time.time() - start_time < timeout:
                # Poll for 1 second
                msgs = await consumer.getmany(timeout_ms=1000)

                for tp, messages in msgs.items():
                    if messages:
                        # Return the latest message
                        result = messages[-1]
                        return result

                # Wait a bit before polling again
                await asyncio.sleep(0.5)

            return None  # Timeout reached
        finally:
            await consumer.stop()

    return _wait_for_result