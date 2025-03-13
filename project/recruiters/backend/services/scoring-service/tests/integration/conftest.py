# tests/conftest.py
import logging
import os
import threading
import time
from pathlib import Path

import pytest
from confluent_kafka import Consumer, KafkaException
from testcontainers.kafka import KafkaContainer
from testcontainers.postgres import PostgresContainer

from src.main import main

logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)


# @pytest.fixture(scope="session")
# def event_loop():
#     loop = asyncio.get_event_loop_policy().new_event_loop()
#     yield loop
#     loop.close()


@pytest.fixture(scope="module")
def postgres_container(request):
    script = Path(__file__).parent / "sql" / "init.sql"
    postgres = (PostgresContainer("postgres:14")
                .with_env("POSTGRES_PORT", "5432")
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
def setup_topics(kafka_container, setup_environment_variables):
    from confluent_kafka.admin import (AdminClient, NewTopic)
    admin = AdminClient({'bootstrap.servers': kafka_container} )
    applications_analyzed_topic = os.getenv("KAFKA_INPUT_TOPIC")
    applications_scored_topic = os.getenv("KAFKA_OUTPUT_TOPIC")

    new_applications_analyzed_topic = NewTopic(applications_analyzed_topic, num_partitions=1, replication_factor=1)
    new_applications_scored_topic = NewTopic(applications_scored_topic, num_partitions=1, replication_factor=1)

    result_dict = admin.create_topics([new_applications_analyzed_topic, new_applications_scored_topic])
    for topic, future in result_dict.items():
        try:
            future.result()  # The result itself is None
            print("Topic {} created".format(topic))
        except Exception as e:
            print("Failed to create topic {}: {}".format(topic, e))

    yield


@pytest.fixture(scope="module")
def setup_environment_variables(postgres_container, kafka_container):
    import os
    os.environ["DB_HOST"] = postgres_container.get_container_host_ip()
    os.environ["DB_PORT"] = str(postgres_container.get_exposed_port(5432))
    os.environ["DB_USER"] = postgres_container.POSTGRES_USER
    os.environ["DB_PASSWORD"] = postgres_container.POSTGRES_PASSWORD
    os.environ["DB_NAME"] = postgres_container.POSTGRES_DB
    os.environ["KAFKA_BOOTSTRAP_SERVERS"] = kafka_container
    os.environ["KAFKA_INPUT_TOPIC"] = "application-analyzed-events"
    os.environ["KAFKA_OUTPUT_TOPIC"] = "application-scored-events"

    yield

@pytest.fixture(scope="module")
def setup_e2e(setup_environment_variables, setup_topics):
    app_thread = threading.Thread(target=main, daemon=True)
    app_thread.start()

    time.sleep(10)

    yield

@pytest.fixture(scope="module")
def wait_for_result_in_kafka(kafka_container):

    consumer = Consumer({
        'bootstrap.servers': kafka_container,
        'group.id': 'test-group',
        'enable.auto.commit': 'false',
        'auto.offset.reset': 'earliest'
    })

    async def _wait_for_result(topic, timeout=300):
        try:
            consumer.subscribe([topic])
            start_time = time.time()
            while time.time() - start_time < timeout:
                msg = consumer.poll(timeout=5.0)
                if msg is None: continue

                if msg.error():
                    logger.error('Unknown error: %s', msg.error())
                    raise KafkaException(msg.error())
                else:
                    return msg
        finally:
            consumer.close()

    return _wait_for_result


# @pytest.mark.asyncio
# async def test_full_scoring_flow(setup_e2e, kafka_container, wait_for_result_in_kafka):
#     """Test the full scoring flow from input event to output event"""
#     # Create a Kafka producer
#     producer = AIOKafkaProducer(
#         bootstrap_servers=kafka_container,
#         value_serializer=lambda v: json.dumps(v).encode('utf-8')
#     )
#     await producer.start()
#
#     # Create a test message
#     test_message = {
#         "id": "test-app-123",
#         "positionId": "pos-123",
#         "application": {
#             "id": "app-123",
#             "candidateId": "cand-123",
#             "description": "Experienced software developer with Python skills",
#             "skills": ["Python", "SQL", "Docker", "Kubernetes"],
#             "experiences": [
#                 {"tasks": ["Developed web applications", "Designed database schemas"]}
#             ]
#         }
#     }
#
#     # Send the message to the input topic
#     try:
#         await producer.send_and_wait("application-analysed-events", test_message)
#         print("Test message sent to Kafka")
#
#         # Wait for the result in the output topic
#         result = await wait_for_result_in_kafka("application-scored-events", timeout=30)
#
#         # Verify the result
#         assert result is not None, "No result received from application"
#         assert result.get("id") is not None
#         assert result.get("applicationId") == "app-123"
#         assert result.get("positionId") == "pos-123"
#         assert "score" in result
#         assert "descScore" in result
#         assert "requirementScore" in result
#         assert "taskScore" in result
#         assert "timeSpent" in result
#
#     finally:
#         await producer.stop()
#
#
#
#
#
#
#
#
#
#
#
# @pytest.fixture(scope="session")
# def db_engine(postgres_container):
#     connection_url = postgres_container.get_connection_url()
#     engine = create_engine(connection_url)
#
#     yield engine
#
# @pytest.fixture(scope="session")
# def db_session_factory(db_engine):
#     """Crea una factory de sesiones para las pruebas"""
#     return sessionmaker(bind=db_engine, expire_on_commit=False)
#
#
# @pytest.fixture(scope="module")
# def db_session(db_session_factory):
#     """Crea una sesión de base de datos para cada test"""
#     session = db_session_factory()
#     yield session
#     session.close()
#
#
# @pytest.fixture(scope="module")
# def position_repository(db_session_factory):
#     """Crea un repositorio de posiciones para pruebas"""
#     return PositionRepository(db_session_factory)
#
#
# @pytest.fixture(scope="module")
# def kafka_producer(kafka_container):
#     """Crea un productor de Kafka para pruebas"""
#     producer = KafkaProducer(kafka_container)
#     producer.initialize()
#     yield producer
#     producer.close()
#
#

#
#
# @pytest.fixture(scope="module")
# def scoring_service():
#     """Crea un servicio de puntuación para pruebas"""
#     return ScoringService()
#
#
# @pytest.fixture(scope="module")
# def process_application_use_case(position_repository, kafka_producer, scoring_service):
#     use_case = ScoreApplicationUseCase(
#         position_repository=position_repository,
#         event_producer=kafka_producer,
#         scoring_service=scoring_service
#     )
#     return use_case
#
#
