import { Module } from '@nestjs/common';
import { ApplicationsService } from './adapters/services/applications-service/applications.service';
import { ApplicationsController } from './api/applications.controller';
import { HttpModule } from '@nestjs/axios';
import { SharedModule } from '../shared/shared.module';
import { PositionsService } from '../positions/adapters/services/position-service/positions.service';
import { PositionsModule } from '../positions/positions.module';

@Module({
  imports: [HttpModule, PositionsModule, SharedModule],
  controllers: [ApplicationsController],
  providers: [ApplicationsService, PositionsService],
})
export class ApplicationsModule {}