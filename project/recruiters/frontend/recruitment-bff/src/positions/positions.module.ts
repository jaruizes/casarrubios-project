import { Module } from '@nestjs/common';
import { HttpModule } from '@nestjs/axios';
import { PositionsService } from './adapters/services/position-service/positions.service';
import { PositionsController } from './api/positions.controller';

@Module({
  controllers: [PositionsController],
  imports: [HttpModule],
  providers: [PositionsService],
})
export class PositionsModule {}
