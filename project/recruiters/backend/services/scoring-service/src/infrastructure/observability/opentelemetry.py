import os

from opentelemetry import trace
from opentelemetry.exporter.otlp.proto.grpc.trace_exporter import OTLPSpanExporter
from opentelemetry.instrumentation.asyncpg import AsyncPGInstrumentor
from opentelemetry.instrumentation.logging import LoggingInstrumentor
from opentelemetry.sdk.resources import SERVICE_NAME, Resource
from opentelemetry.sdk.trace import TracerProvider
from opentelemetry.sdk.trace.export import BatchSpanProcessor
from opentelemetry.sdk.trace.sampling import ALWAYS_ON

resource = Resource.create({
    "service.name": "scoring-service"
})
tracer_provider = TracerProvider(resource=resource, sampler=ALWAYS_ON)

def setup_telemetry(otlp_endpoint):
    trace.set_tracer_provider(tracer_provider)

    if otlp_endpoint:
        otlp_exporter = OTLPSpanExporter(endpoint=otlp_endpoint + "/v1/traces")
        span_processor = BatchSpanProcessor(otlp_exporter)
        provider.add_span_processor(span_processor)

    AsyncPGInstrumentor().instrument()
    LoggingInstrumentor().instrument()
    HTTPXClientInstrumentor().instrument()

    logger.info(f"Tracing started (endpoint = {otel_endpoint} up database connection")

    return provider


def shutdown_telemetry():
    tracer_provider.shutdown()