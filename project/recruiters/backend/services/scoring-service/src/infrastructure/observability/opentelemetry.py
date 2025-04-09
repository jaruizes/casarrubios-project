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

def setup_telemetry(otlp_endpoint):
    trace.set_tracer_provider(tracer_provider)

    if otlp_endpoint:
        otel_endpoint_complete = otlp_endpoint + "/v1/traces"
        otlp_exporter = OTLPSpanExporter(endpoint=otel_endpoint_complete)
        span_processor = BatchSpanProcessor(otlp_exporter)
        tracer_provider.add_span_processor(span_processor)

    AsyncPGInstrumentor().instrument()
    LoggingInstrumentor().instrument()
    HTTPXClientInstrumentor().instrument()
    KafkaInstrumentor().instrument()

    logger.info(f"Tracing started (endpoint = {otel_endpoint_complete})")

    return tracer_provider


def shutdown_telemetry():
    tracer_provider.shutdown()