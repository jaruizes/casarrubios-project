import { NestFactory } from '@nestjs/core';
import { AppModule } from './app.module';
import { ConsoleLogger } from '@nestjs/common';
import { otelSDK } from './instrumentation';

async function bootstrap() {
  otelSDK.start();

  const app = await NestFactory.create(AppModule, {
    cors: true,
    logger: new ConsoleLogger({
      prefix: 'Applications-BFF',
    }),
  });

  await app.listen(process.env.PORT ?? 3000);
}
bootstrap();