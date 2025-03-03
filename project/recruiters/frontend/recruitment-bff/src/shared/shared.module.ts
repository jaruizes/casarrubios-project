import { Module } from '@nestjs/common';
import { Config } from './config/config';
import { ConfigModule } from '@nestjs/config';

@Module({
  imports: [ConfigModule],
  providers: [Config],
  exports: [Config]
})
export class SharedModule {}
