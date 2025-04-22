import { Module } from '@nestjs/common';
import { HttpModule } from '@nestjs/axios';
import { SharedModule } from '../shared/shared.module';
import { InsightsController } from './api/insights.controller';
import { InsightsService } from './adapters/services/insights-service/insights.service';

@Module({
  controllers: [InsightsController],
  imports: [HttpModule, SharedModule],
  providers: [InsightsService],
})
export class InsightsModule {}
