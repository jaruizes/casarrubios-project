import asyncio
import logging
import signal
import sys

import openai

from src.application.adapters.db.sqlalchemy_repository import PositionRepository
from src.application.api.scoring_service_async_api import ScoringServiceAsyncAPI
from src.config import load_config
from src.domain.services.scoring_service import ScoringService
from src.infrastructure.db.sqlalchemy_connection import SQLAlchemyConnection
from src.infrastructure.kafka.kafka_consumer import KafkaConsumer
from src.infrastructure.kafka.kafka_producer import KafkaProducer
from src.infrastructure.observability.opentelemetry import setup_telemetry, shutdown_telemetry


# Configuración de logging
def setup_logging(log_level: str):
    logging.basicConfig(
        level=getattr(logging, log_level.upper()),
        format='%(asctime)s - %(name)s - %(levelname)s - %(message)s',
        handlers=[logging.StreamHandler()]
    )

def shutdown(
        consumer: KafkaConsumer,
        producer: KafkaProducer,
        db_connection: SQLAlchemyConnection):
    logging.info("Shutting down application...")

    consumer.stop()
    producer.close()
    db_connection.disconnect()
    shutdown_telemetry()
    logging.info("Application shutdown complete")


def startup():
    config = load_config()

    setup_logging(config.log_level)
    logger = logging.getLogger(__name__)
    logger.info("Starting Application Scoring Service")

    if config.telemetry.enabled:
        logger.info("Setting up OpenTelemetry...")
        setup_telemetry()

    logger.info("Connecting to database...")
    db_connection = SQLAlchemyConnection(
        host=config.db.host,
        port=config.db.port,
        user=config.db.user,
        password=config.db.password,
        database=config.db.database
    )
    session_factory = db_connection.connect()
    position_repository = PositionRepository(session_factory)

    logger.info("Initializing Kafka producer...")
    producer = KafkaProducer(bootstrap_servers=config.kafka.bootstrap_servers)
    consumer = KafkaConsumer(bootstrap_servers=config.kafka.bootstrap_servers,
                             topic=config.kafka.input_topic,
                             group_id=config.kafka.consumer_group)

    logger.info("Initializing Scoring Service...")
    scoring_service = ScoringService(position_repository=position_repository,
                                     producer=producer,
                                     output_topic=config.kafka.output_topic)

    logger.info("Initializing Open AI...")
    openai.api_key = config.openai_key

    logger.info("Initializing API...")
    scoring_service_async_api = ScoringServiceAsyncAPI(scoring_service=scoring_service)
    consumer.start(scoring_service_async_api.handle_application_analyzed_event)

    loop = asyncio.get_running_loop()
    for sig in (signal.SIGINT, signal.SIGTERM):
        loop.add_signal_handler(
            sig,
            lambda: asyncio.create_task(shutdown(consumer, producer, db_connection))
        )

    logger.info("Application Scoring Service started successfully")

def main():
    try:
        startup()
    except KeyboardInterrupt:
        print("Application stopped by user")
    except Exception as e:
        print(f"Application error: {str(e)}")
        sys.exit(1)


if __name__ == "__main__":
    main()