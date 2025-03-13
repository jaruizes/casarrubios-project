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

def load_config() -> ApplicationConfig:
    return ApplicationConfig(
        host=os.getenv("DB_HOST", "localhost"),
        port=int(os.getenv("DB_PORT", "5432")),
        user=os.getenv("DB_USER", "test"),
        password=os.getenv("DB_PASSWORD", "test"),
        database=os.getenv("DB_NAME", "test"),
        schema=os.getenv("DB_SCHEMA", "test"),
        bootstrap_servers=os.getenv("KAFKA_BOOTSTRAP_SERVERS", "localhost:9092"),
        consumer_group=os.getenv("KAFKA_CONSUMER_GROUP", "application-scoring-service"),
        offset_reset=os.getenv("KAFKA_CONSUMER_OFFSET_RESET", "earliest"),
        input_topic=os.getenv("KAFKA_INPUT_TOPIC", "application-analysed-events"),
        log_level=os.getenv("LOG_LEVEL", "INFO")
    )