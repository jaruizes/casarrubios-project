import { Module } from '@nestjs/common';
import { HttpModule } from '@nestjs/axios';
import { SharedModule } from '../shared/shared.module';
import { GlobalPositionController } from './api/global-position.controller';
import { GlobalPositionService } from './adapters/services/global-position-service/global-position.service';

@Module({
  controllers: [GlobalPositionController],
  imports: [HttpModule, SharedModule],
  providers: [GlobalPositionService],
})
export class GlobalPositionModule {}
