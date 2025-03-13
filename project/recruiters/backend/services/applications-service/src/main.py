import asyncio
import logging
import signal
import sys

from src.adapters.db.repositories import ApplicationRepository
from src.api.input.message.application_scored_event_handler import ApplicationScoredEventHandler
from src.domain.services.application_service import ApplicationService
from src.infrastructure.core.config import load_config
from src.infrastructure.db.sqlalchemy_connection import SQLAlchemyConnection
from src.infrastructure.kafka.kafka_consumer import KafkaConsumer
from fastapi import FastAPI

from src.api.input.rest import applications_rest_api
from fastapi.middleware.cors import CORSMiddleware
from opentelemetry.instrumentation.fastapi import FastAPIInstrumentor


def setup_logging(log_level: str):
    logging.basicConfig(
        level=getattr(logging, log_level.upper()),
        format='%(asctime)s - %(name)s - %(levelname)s - %(message)s',
        handlers=[logging.StreamHandler(sys.stdout)]
    )

def shutdown(
        consumer: KafkaConsumer,
        db_connection: SQLAlchemyConnection):
    logging.info("Shutting down application...")

    consumer.stop()
    db_connection.disconnect()
    logging.info("Application shutdown complete")

def configFastAPIApp():
    app = FastAPI(
        title="Recruiters API",
        version="1.0.0",
        description="API for managing applications in the recruiters system"
    )

    FastAPIInstrumentor.instrument_app(app)

    # Configurar CORS para permitir peticiones desde cualquier origen (ajustar en producción)
    app.add_middleware(
        CORSMiddleware,
        allow_origins=["*"],  # Cambia esto en producción
        allow_credentials=True,
        allow_methods=["*"],
        allow_headers=["*"],
    )

    app.include_router(applications_rest_api.router, prefix="", tags=["applications"])

    # Ruta de salud para verificar que la API está corriendo
    @app.get("/")
    def health_check():
        return {"status": "running"}


def startup():
    setup_logging('INFO')
    logger = logging.getLogger(__name__)
    logger.info('Starting up')
    configFastAPIApp()

    config = load_config()

    db_connection = SQLAlchemyConnection(
        host=config.host,
        port=config.port,
        user=config.user,
        password=config.password,
        database=config.database
    )
    session_factory = db_connection.connect()
    application_repository = ApplicationRepository(session_factory)
    application_service = ApplicationService(repository=application_repository)
    application_scored_event_handler = ApplicationScoredEventHandler(
        applications_service=application_service
    )

    consumer = KafkaConsumer(
        bootstrap_servers=config.bootstrap_servers,
        topic=config.input_topic,
        group_id=config.consumer_group
    )
    consumer.start(application_scored_event_handler.handle_application_scored_event)

    loop = asyncio.get_running_loop()
    for sig in (signal.SIGINT, signal.SIGTERM):
        loop.add_signal_handler(
            sig,
            lambda: asyncio.create_task(shutdown(consumer, db_connection))
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


