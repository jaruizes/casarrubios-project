import os
from dataclasses import dataclass


@dataclass
class ApplicationConfig:
    host: str
    port: int
    user: str
    password: str
    database: str
    schema: str
    bootstrap_servers: str
    consumer_group: str
    offset_reset: str
    input_topic: str
    log_level: str
    minio_url: str
    minio_access_name: str
    minio_access_secret: str
    minio_bucket_name: str
    tracing_enabled: bool

def load_config() -> ApplicationConfig:
    return ApplicationConfig(
        host=os.getenv("DB_HOST", "localhost"),
        port=int(os.getenv("DB_PORT", "5432")),
        user=os.getenv("DB_USER", "postgres"),
        password=os.getenv("DB_PASSWORD", "postgres"),
        database=os.getenv("DB_NAME", "applications"),
        schema=os.getenv("DB_SCHEMA", "recruiters"),
        bootstrap_servers=os.getenv("KAFKA_BOOTSTRAP_SERVERS", "localhost:29092"),
        consumer_group=os.getenv("KAFKA_CONSUMER_GROUP", "application-scoring-service"),
        offset_reset=os.getenv("KAFKA_CONSUMER_OFFSET_RESET", "earliest"),
        input_topic=os.getenv("KAFKA_INPUT_TOPIC", "recruiters.application-scored"),
        log_level=os.getenv("LOG_LEVEL", "INFO"),
        minio_url=os.getenv("MINIO_URL", "INFO"),
        minio_access_name=os.getenv("MINIO_ACCESS_NAME", "minioadmin"),
        minio_access_secret=os.getenv("MINIO_ACCESS_SECRET", "minioadmin"),
        minio_bucket_name=os.getenv("MINIO_BUCKET_NAME", "files"),
        tracing_enabled=os.getenv("TRACING_ENABLED", "false").lower() == "true"
    )
