import os
from dataclasses import dataclass, field


@dataclass
class DatabaseConfig:
    host: str
    port: int
    user: str
    password: str
    database: str


@dataclass
class KafkaConfig:
    bootstrap_servers: str
    consumer_group: str
    offset_reset: str
    input_topic: str
    output_topic: str

@dataclass
class VectorStorageConfig:
    host: str
    port: int


@dataclass
class TelemetryConfig:
    enabled: bool
    otlp_endpoint: str


@dataclass
class ApplicationConfig:
    log_level: str
    openai_key: str
    db: DatabaseConfig = field(default_factory=DatabaseConfig)
    kafka: KafkaConfig = field(default_factory=KafkaConfig)
    vector_storage: VectorStorageConfig = field(default_factory=VectorStorageConfig)
    telemetry: TelemetryConfig = field(default_factory=TelemetryConfig)


def load_config() -> ApplicationConfig:
    appconfig = ApplicationConfig(
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
            enabled=os.getenv("TELEMETRY_ENABLED", "true").lower() == "false",
            otlp_endpoint=os.getenv("OTEL_EXPORTER_OTLP_ENDPOINT")
        ),
        vector_storage=VectorStorageConfig(
            host=os.getenv("VECTOR_STORAGE_HOST", "localhost"),
            port=int(os.getenv("VECTOR_STORAGE_PORT", "6333"))
        ),
        log_level=os.getenv("LOG_LEVEL", "INFO"),
        openai_key=os.getenv("OPENAI_API_KEY")
    )

    return appconfig