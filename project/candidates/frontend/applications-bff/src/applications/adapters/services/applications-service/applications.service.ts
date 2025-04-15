import { Injectable, Logger, NotFoundException } from '@nestjs/common';
import { HttpService } from '@nestjs/axios';
import { ConfigService } from '@nestjs/config';
import { ApplicationDTO, ApplicationResponseDTO } from './dto/application.dto';
import { ApplicationsBackendNotAvailableException } from '../../../exceptions/applications-backend-not-available.exception';
import { firstValueFrom } from 'rxjs';
import { NewApplicationDTO } from '../../../api/dto/new-application.dto';
import * as FormData from 'form-data';

@Injectable()
export class ApplicationsService {
  private backendUrl: string;
  private readonly logger = new Logger(ApplicationsService.name);

  constructor(private httpService: HttpService, private readonly configService: ConfigService) {
    this.backendUrl = this.configService.get<string>('APPLICATIONS_BACKEND_URL', 'http://localhost:8090');
    this.logger.log(`[Applications Service] Backend URL set to: ${this.backendUrl}`);
  }

  async sendApplication(newApplication: NewApplicationDTO, file: Express.Multer.File): Promise<ApplicationResponseDTO> {
    const url = `${this.backendUrl}/applications`;
    this.logger.debug(`[Applications Service] Trying to send new application to: ${url}`);

    const formData = new FormData();
    formData.append('positionId', newApplication.positionId.toString());
    formData.append('candidate', newApplication.candidate, { contentType: 'application/json' });
    formData.append('cvFile', file.buffer, { filename: file.originalname, contentType: file.mimetype });

    console.log('------------------------------------');
    console.log(formData.getHeaders());
    console.log(formData);
    console.log('------------------------------------');

    try {
      const config = {
        headers: {
          'Accept': 'application/json',
          'content-type': 'multipart/form-data'
        }
      }
      const response = await firstValueFrom(
        this.httpService.post(url, formData, config),
      );

      this.logger.debug(`[Applications Service] Received response: ${JSON.stringify(response.data)}`);

      return response.data;
    } catch (error) {
      if (error.isAxiosError && error.code === 'ECONNREFUSED') {
        throw new ApplicationsBackendNotAvailableException();
      }

      this.logger.error(`Error fetching positions: ${error.message}`, error.stack);
      throw new Error(`Error fetching positions: ${error.message}`);
    }
  }
}