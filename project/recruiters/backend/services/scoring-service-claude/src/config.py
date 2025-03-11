import os
from dataclasses import dataclass
from typing import Optional


@dataclass
class DatabaseConfig:
    host: str = os.getenv("DB_HOST", "localhost")
    port: int = int(os.getenv("DB_PORT", "5432"))
    user: str = os.getenv("DB_USER", "test")
    password: str = os.getenv("DB_PASSWORD", "test")
    database: str = os.getenv("DB_NAME", "test")


@dataclass
class KafkaConfig:
    bootstrap_servers: str = os.getenv("KAFKA_BOOTSTRAP_SERVERS", "localhost:9092")
    consumer_group: str = os.getenv("KAFKA_CONSUMER_GROUP", "application-scoring-service")
    offset_reset: str = os.getenv("KAFKA_CONSUMER_OFFSET_RESET", "earliest")
    input_topic: str = os.getenv("KAFKA_INPUT_TOPIC", "application-analysed-events")
    output_topic: str = os.getenv("KAFKA_OUTPUT_TOPIC", "application-scored-events")


@dataclass
class TelemetryConfig:
    enabled: bool = os.getenv("TELEMETRY_ENABLED", "true").lower() == "true"
    otlp_endpoint: Optional[str] = os.getenv("OTLP_EXPORTER_ENDPOINT")


@dataclass
class ApplicationConfig:
    db: DatabaseConfig = DatabaseConfig()
    kafka: KafkaConfig = KafkaConfig()
    telemetry: TelemetryConfig = TelemetryConfig()
    log_level: str = os.getenv("LOG_LEVEL", "INFO")
    openai_key: str = os.getenv("OPENAI_API_KEY")


def load_config() -> ApplicationConfig:
    appconfig =  ApplicationConfig(
        db=DatabaseConfig(
            host=os.getenv("DB_HOST", "localhost"),
            port=int(os.getenv("DB_PORT", "5432")),
            user=os.getenv("DB_USER", "test"),
            password=os.getenv("DB_PASSWORD", "test"),
            database=os.getenv("DB_NAME", "test")
        ),
        kafka=KafkaConfig(
            bootstrap_servers=os.getenv("KAFKA_BOOTSTRAP_SERVERS", "localhost:9092"),
            consumer_group=os.getenv("KAFKA_CONSUMER_GROUP", "application-scoring-service"),
            offset_reset=os.getenv("KAFKA_CONSUMER_OFFSET_RESET", "earliest"),
            input_topic=os.getenv("KAFKA_INPUT_TOPIC", "application-analysed-events"),
            output_topic=os.getenv("KAFKA_OUTPUT_TOPIC", "application-scored-events")
        ),
        telemetry=TelemetryConfig(
            enabled=os.getenv("TELEMETRY_ENABLED", "true").lower() == "true",
            otlp_endpoint=os.getenv("OTLP_EXPORTER_ENDPOINT")
        ),
        log_level=os.getenv("LOG_LEVEL", "INFO"),
        openai_key=os.getenv("OPENAI_API_KEY")
    )

    return appconfig