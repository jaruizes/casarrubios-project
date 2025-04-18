import os
import logging

from opentelemetry import trace
from opentelemetry.exporter.otlp.proto.grpc.trace_exporter import OTLPSpanExporter
from opentelemetry.instrumentation.asyncpg import AsyncPGInstrumentor
from opentelemetry.instrumentation.httpx import HTTPXClientInstrumentor
from opentelemetry.instrumentation.kafka import KafkaInstrumentor
from opentelemetry.instrumentation.logging import LoggingInstrumentor
from opentelemetry.sdk.resources import Resource
from opentelemetry.sdk.trace import TracerProvider
from opentelemetry.sdk.trace.export import BatchSpanProcessor
from opentelemetry.sdk.trace.sampling import ALWAYS_ON

logger = logging.getLogger(__name__)
resource = Resource.create({
    "service.name": "recruitment-scoring-service"
})
tracer_provider = TracerProvider(resource=resource, sampler=ALWAYS_ON)

def setup_telemetry():
    logger.info("Setting up telemetry")

    trace.set_tracer_provider(tracer_provider)
    otlp_endpoint = os.getenv("OTEL_EXPORTER_OTLP_ENDPOINT", "http://localhost:4318")

    if otlp_endpoint:
        span_exporter = OTLPSpanExporter(endpoint=otlp_endpoint + "/v1/traces")
        span_processor = BatchSpanProcessor(span_exporter)
        tracer_provider.add_span_processor(span_processor)

    AsyncPGInstrumentor().instrument()
    LoggingInstrumentor().instrument()
    HTTPXClientInstrumentor().instrument()
    KafkaInstrumentor().instrument()

    logger.info(f"Tracing started (endpoint = {otlp_endpoint}/v1/traces)")

    return tracer_provider


def shutdown_telemetry():
    tracer_provider.shutdown()