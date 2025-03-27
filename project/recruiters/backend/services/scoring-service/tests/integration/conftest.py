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
from unittest.mock import patch, MagicMock
import pytest

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

@pytest.fixture(autouse=True, scope="module")
def mock_openai_embeddings():
    def fake_create_embedding(input, model):
        if isinstance(input, list):
            inputs = input
        else:
            inputs = [input]

        embeddings = []
        for text in inputs:
            # Simula un embedding "determinista" basado en el texto
            vector = [(ord(c) % 10) / 10.0 for c in text][:10]
            padded = vector + [0.0] * (1536 - len(vector))
            embeddings.append(MagicMock(embedding=padded))

        return MagicMock(data=embeddings)

    def fake_chat_completion_create(model, messages, temperature, top_p, n):
        user_prompt = next((msg["content"] for msg in messages if msg["role"] == "user"), "")
        fake_response = MagicMock()
        fake_choice = MagicMock()
        fake_choice.message.content = f"Fake explanation for: {user_prompt}"
        fake_response.choices = [fake_choice]
        return fake_response

    with patch("src.domain.services.scoring_service.openai.embeddings.create", side_effect=fake_create_embedding), \
         patch("src.domain.services.scoring_service.openai.chat.completions.create", side_effect=fake_chat_completion_create):
        yield