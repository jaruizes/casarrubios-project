import asyncio
import logging
import signal
import sys

from src.infrastructure.tracing.otel_tracing import configure_tracer
from src.adapters.cvfiles.minio_cv_service import MinioCVService
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

from src.infrastructure.minio.MinioClient import MinioClient


def setup_logging(log_level: str):
    logging.basicConfig(
        level=getattr(logging, log_level.upper()),
        format='%(asctime)s - %(name)s - %(levelname)s - %(message)s',
        handlers=[logging.StreamHandler(sys.stdout)]
    )

async def shutdown_handler(consumer: KafkaConsumer, db_connection: SQLAlchemyConnection):
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
    
    return app

def startup():
    try:
        setup_logging('INFO')
        logger = logging.getLogger(__name__)
        logger.info('Starting up')

        app = configFastAPIApp()
        config = load_config()


        if config.tracing_enabled:
            configure_tracer(app)

        logger.info('Starting up database connection')
        db_connection = SQLAlchemyConnection(
            host=config.host,
            port=config.port,
            user=config.user,
            password=config.password,
            database=config.database
        )
        session_factory = db_connection.connect()
        application_repository = ApplicationRepository(session_factory)

        logger.info('Starting up Minio client')
        minio_client = MinioClient(
            endpoint=config.minio_url,
            access_key=config.minio_access_name,
            secret_key=config.minio_access_secret
        )
        cv_service = MinioCVService(minio_client, config.minio_bucket_name)

        logger.info('Starting up application service')
        application_service = ApplicationService(repository=application_repository, cv_service=cv_service)

        logger.info('Starting up application scored event handler')
        application_scored_event_handler = ApplicationScoredEventHandler(
            applications_service=application_service
        )

        logger.info('Starting up Kafka Consumer')
        consumer = KafkaConsumer(
            bootstrap_servers=config.bootstrap_servers,
            topic=config.input_topic,
            group_id=config.consumer_group
        )
        consumer.start(application_scored_event_handler.handle_application_scored_event)

        logger.info('Starting up running loop')
        loop = asyncio.get_running_loop()
        for sig in (signal.SIGINT, signal.SIGTERM):
            loop.add_signal_handler(
                sig,
                lambda: asyncio.create_task(shutdown_handler(consumer, db_connection))
            )

        logger.info("Applications Service started successfully")
    
        return app
    except Exception as e:
        print(f"Application error: {str(e)}")
        raise e

def main():
    try:
        import uvicorn
        import os
        
        # Usar el puerto de la variable de entorno o 8000 por defecto
        port = int(os.environ.get("PORT", 8000))

        uvicorn.run(
            "src.main:startup",
            host="0.0.0.0",
            port=port,
            factory=True,
            reload=True
        )
    except KeyboardInterrupt:
        print("Application stopped by user")
    except Exception as e:
        print(f"Application error: {str(e)}")
        sys.exit(1)


if __name__ == "__main__":
    main()
