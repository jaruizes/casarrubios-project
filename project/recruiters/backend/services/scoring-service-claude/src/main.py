# src/main.py
import asyncio
import logging
import signal
import sys
from typing import Dict, Any
from functools import partial

from src.config import load_config
from src.infrastructure.db.sqlalchemy_connection import SQLAlchemyConnection
from src.infrastructure.observability.opentelemetry import setup_telemetry, shutdown_telemetry
from src.application.adapters.db.sqlalchemy_repository import SQLAlchemyPositionRepository
from src.application.adapters.messaging.kafka_consumer import KafkaConsumer
from src.application.adapters.messaging.kafka_producer import KafkaProducer
from src.domain.services.scoring_service import ScoringService
from src.application.usecases.process_application import ProcessApplicationUseCase


# Configuración de logging
def setup_logging(log_level: str):
    logging.basicConfig(
        level=getattr(logging, log_level.upper()),
        format='%(asctime)s - %(name)s - %(levelname)s - %(message)s',
        handlers=[logging.StreamHandler()]
    )


async def process_application_event(
        event_data: Dict[str, Any],
        use_case: ProcessApplicationUseCase
):
    """Handler para procesar eventos de aplicación"""
    await use_case.execute(event_data)


async def shutdown(
        consumer: KafkaConsumer,
        producer: KafkaProducer,
        db_connection: SQLAlchemyConnection
):
    """Cierra ordenadamente los recursos de la aplicación"""
    logging.info("Shutting down application...")

    # Detener el consumidor de Kafka
    await consumer.stop()

    # Cerrar el productor de Kafka
    await producer.close()

    # Cerrar la conexión a la base de datos
    db_connection.disconnect()

    # Limpiar recursos de telemetría
    shutdown_telemetry()

    logging.info("Application shutdown complete")


async def startup():
    """Inicia la aplicación y configura las dependencias"""
    # Cargar configuración
    config = load_config()

    # Configurar logging
    setup_logging(config.log_level)
    logger = logging.getLogger(__name__)
    logger.info("Starting Application Scoring Service")

    # Configurar telemetría si está habilitada
    if config.telemetry.enabled:
        logger.info("Setting up OpenTelemetry...")
        setup_telemetry()

    # Establecer conexión a la base de datos
    logger.info("Connecting to database...")
    db_connection = SQLAlchemyConnection(
        host=config.db.host,
        port=config.db.port,
        user=config.db.user,
        password=config.db.password,
        database=config.db.database
    )
    session_factory = db_connection.connect()

    # Crear instancias de repositorios
    position_repository = SQLAlchemyPositionRepository(session_factory)

    # Crear productor de Kafka
    logger.info("Initializing Kafka producer...")
    producer = KafkaProducer(config.kafka.bootstrap_servers)
    await producer.initialize()

    # Crear servicio de puntuación
    scoring_service = ScoringService()

    # Crear caso de uso
    process_application_use_case = ProcessApplicationUseCase(
        position_repository=position_repository,
        event_producer=producer,
        scoring_service=scoring_service
    )

    # Crear consumidor de Kafka y suscribirse al topic de entrada
    logger.info(f"Setting up Kafka consumer for topic {config.kafka.input_topic}...")
    consumer = KafkaConsumer(
        bootstrap_servers=config.kafka.bootstrap_servers,
        group_id=config.kafka.consumer_group
    )

    # Crear un handler parcial con el caso de uso inyectado
    handler = partial(process_application_event, use_case=process_application_use_case)

    # Suscribirse al topic
    await consumer.subscribe(config.kafka.input_topic, handler)

    # Configurar manejo de señales para cierre ordenado
    loop = asyncio.get_running_loop()
    for sig in (signal.SIGINT, signal.SIGTERM):
        loop.add_signal_handler(
            sig,
            lambda: asyncio.create_task(shutdown(consumer, producer, db_connection))
        )

    logger.info("Application Scoring Service started successfully")

    # Iniciar el consumidor
    await consumer.start()


def main():
    """Punto de entrada principal de la aplicación"""
    try:
        asyncio.run(startup())
    except KeyboardInterrupt:
        print("Application stopped by user")
    except Exception as e:
        print(f"Application error: {str(e)}")
        sys.exit(1)


if __name__ == "__main__":
    main()