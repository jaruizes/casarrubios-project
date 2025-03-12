import os

from opentelemetry import trace
from opentelemetry.exporter.otlp.proto.grpc.trace_exporter import OTLPSpanExporter
from opentelemetry.instrumentation.asyncpg import AsyncPGInstrumentor
from opentelemetry.instrumentation.logging import LoggingInstrumentor
from opentelemetry.sdk.resources import SERVICE_NAME, Resource
from opentelemetry.sdk.trace import TracerProvider
from opentelemetry.sdk.trace.export import BatchSpanProcessor

# Configurar recurso con nombre del servicio
resource = Resource(attributes={
    SERVICE_NAME: "application-scoring-service"
})

# Configurar proveedor de trazas
provider = TracerProvider(resource=resource)
trace.set_tracer_provider(provider)

# Crear tracer para la aplicación
tracer = trace.get_tracer(__name__)


def setup_telemetry():
    """Configura OpenTelemetry con exportadores y procesadores"""
    # Configurar exportador OTLP si se proporciona un endpoint
    otlp_endpoint = os.getenv("OTLP_EXPORTER_ENDPOINT")
    if otlp_endpoint:
        otlp_exporter = OTLPSpanExporter(endpoint=otlp_endpoint)
        span_processor = BatchSpanProcessor(otlp_exporter)
        provider.add_span_processor(span_processor)

    # Instrumentación para AsyncPG
    AsyncPGInstrumentor().instrument()

    # Instrumentación para logging
    LoggingInstrumentor().instrument()

    return provider


def shutdown_telemetry():
    """Limpia los recursos de telemetría"""
    provider.shutdown()