import { NodeSDK } from '@opentelemetry/sdk-node';
import { getNodeAutoInstrumentations } from '@opentelemetry/auto-instrumentations-node';
import { OTLPTraceExporter } from '@opentelemetry/exporter-trace-otlp-http';

const otelURLBase = process.env.OTEL_EXPORTER_OTLP_ENDPOINT || 'http://localhost:4318';
const otelExporterEndpoint = otelURLBase + '/v1/traces';
const traceExporter = new OTLPTraceExporter({ url: otelExporterEndpoint });

export const otelSDK = new NodeSDK({
  traceExporter: traceExporter,
  instrumentations: [getNodeAutoInstrumentations()],
  serviceName: 'applications-bff',
});

otelSDK.start();
console.log('OpenTelemetry Node SDK started');
console.log('otelExporterEndpoint: ' + otelExporterEndpoint);

process.on('SIGTERM', () => {
  otelSDK
    .shutdown()
    .then(
      () => console.log('SDK shut down successfully'),
      (err) => console.log('Error shutting down SDK', err),
    )
    .finally(() => process.exit(0));
});
