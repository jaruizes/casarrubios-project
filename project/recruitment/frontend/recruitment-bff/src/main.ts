import { NestFactory } from '@nestjs/core';
import { AppModule } from './app.module';
import { ConsoleLogger } from '@nestjs/common';
import { MicroserviceOptions, Transport } from '@nestjs/microservices';
import { otelSDK } from './instrumentation';

async function bootstrap() {
  otelSDK.start();
  const app = await NestFactory.create(AppModule, {
    cors: true,
    logger: new ConsoleLogger({
      prefix: 'Recruitment-BFF',
    }),
  });

  const brokers = (process.env.KAFKA_BROKERS ?? 'localhost:9092').split(',');

  app.connectMicroservice<MicroserviceOptions>({
    transport: Transport.KAFKA,
    options: {
      client: {
        clientId: 'recruitment-bff',
        brokers: brokers,
      },
      consumer: {
        groupId: 'recruitment-group',
      },
    },
  });
  await app.startAllMicroservices();
  await app.listen(process.env.PORT ?? 4000);
}

bootstrap();
