import { Module } from '@nestjs/common';
import { ApplicationsService } from './adapters/services/applications-service/applications.service';
import { ApplicationsController } from './api/applications.controller';
import { HttpModule } from '@nestjs/axios';

@Module({
  imports: [HttpModule],
  controllers: [ApplicationsController],
  providers: [ApplicationsService],
})
export class ApplicationsModule {}