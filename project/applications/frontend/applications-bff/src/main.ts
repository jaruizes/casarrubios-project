import { NestFactory } from '@nestjs/core';
import { AppModule } from './app.module';
import { ConsoleLogger } from '@nestjs/common';
import { otelSDK } from './instrumentation';
import { context, trace } from '@opentelemetry/api';

async function bootstrap() {
  otelSDK.start();

  const tracer = trace.getTracer('bootstrap-tracer');

  await context.with(
    trace.setSpan(context.active(), tracer.startSpan('bootstrap-span')),
    async () => {
      const app = await NestFactory.create(AppModule, {
        cors: true,
        logger: new ConsoleLogger({
          prefix: 'Applications-BFF',
        }),
      });
      await app.listen(process.env.PORT ?? 3000);
      trace.getSpan(context.active())?.end();
    },
  );
}
bootstrap();
