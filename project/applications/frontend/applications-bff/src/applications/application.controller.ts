import { Body, Controller, Post } from '@nestjs/common';
import { ApplicationDTO } from './application.dto';
import { ApplicationService } from './application.service';

@Controller('applications')
export class ApplicationController {
  constructor(private applicationService: ApplicationService) {}

  @Post()
  createApplication(@Body() applicationDTO: ApplicationDTO): void {
    this.applicationService.createApplication(applicationDTO);
  }
}