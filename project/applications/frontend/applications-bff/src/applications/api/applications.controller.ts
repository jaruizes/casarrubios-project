import {
  Body,
  Controller,
  HttpException,
  HttpStatus,
  Logger,
  Post,
  UploadedFile,
  UseInterceptors,
} from '@nestjs/common';
import { NewApplicationDTO, NewApplicationResponseDTO } from './dto/new-application.dto';
import { ApplicationsService } from '../adapters/services/applications-service/applications.service';
import { ErrorDTO } from '../../positions/api/dto/error.dto';
import { ApplicationsBackendNotAvailableException } from '../exceptions/applications-backend-not-available.exception';
import { FileInterceptor } from '@nestjs/platform-express';
import { Express } from 'express';

@Controller('applications')
export class ApplicationsController {
  private readonly logger = new Logger(ApplicationsController.name);

  constructor(private readonly applicationsService: ApplicationsService) {}

  @Post()
  @UseInterceptors(FileInterceptor('cv'))
  async sendApplication(@Body() newApplication: NewApplicationDTO, @UploadedFile() file: Express.Multer.File): Promise<NewApplicationResponseDTO | undefined> {
    this.logger.log(`${JSON.stringify(newApplication)}`);
    this.logger.log(file);
    this.logger.log(`Trying to send application for position with id:`);

    try {
      const application = await this.applicationsService.sendApplication(newApplication, file);
      this.logger.log(`Application sent for position with id: ${newApplication.positionId}`);
      return new NewApplicationResponseDTO(application.applicationId);
    } catch (error) {
      if (error instanceof ApplicationsBackendNotAvailableException) {
        this.logger.log(`Error sending application for position with id (${newApplication.positionId}). Code: ${error.code}, Message: ${error.message}`);
        throw new HttpException(new ErrorDTO(error.code, error.message), HttpStatus.SERVICE_UNAVAILABLE);
      }

      this.logger.error(`Unexpected error: ${error.message}`, error.stack);
      throw new HttpException(new ErrorDTO(500, 'Internal Server Error'), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}