import { Module } from '@nestjs/common';
import { HttpModule } from '@nestjs/axios';
import { PositionsService } from './adapters/services/position-service/positions.service';
import { PositionsController } from './api/positions.controller';
import { SharedModule } from '../shared/shared.module';
import { Config } from '../shared/config/config';

@Module({
  controllers: [PositionsController],
  imports: [HttpModule, SharedModule],
  providers: [PositionsService],
})
export class PositionsModule {}
