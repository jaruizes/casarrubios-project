import { Injectable, Logger, NotFoundException } from '@nestjs/common';
import { HttpService } from '@nestjs/axios';
import {
  ServiceApplicationDTO,
  ServicePaginatedApplicationsDTO,
} from './dto/application.dto';
import { ApplicationsBackendNotAvailableException } from '../../../exceptions/applications-backend-not-available.exception';
import { firstValueFrom } from 'rxjs';
import { Config } from '../../../../shared/config/config';

@Injectable()
export class ApplicationsService {
  private readonly backendUrl: string;
  private readonly logger = new Logger(ApplicationsService.name);

  constructor(private httpService: HttpService, private readonly config: Config) {
    this.backendUrl = this.config.getApplicationsBackendUrl();
    this.logger.log(`[Applications Service] Backend URL set to: ${this.backendUrl}`);
  }

  async getApplicationById(applicationId: string): Promise<ServiceApplicationDTO> {
    const url = `${this.backendUrl}/applications/${applicationId}`;
    this.logger.debug(`[Applications Service] Trying to fetch application from: ${url}`);

    try {
      const response = await firstValueFrom(
        this.httpService.get<ServiceApplicationDTO>(url),
      );
      this.logger.debug(`[Applications Service] Received response: ${JSON.stringify(response.data)}`);

      return response.data;
    } catch (error) {
      if (error.isAxiosError && error.response.status === 404) {
        throw new NotFoundException(`Application with id ${applicationId} not found`);
      }

      if (error.isAxiosError && error.code === 'ECONNREFUSED') {
        throw new ApplicationsBackendNotAvailableException();
      }

      this.logger.error(`Error fetching application: ${error.message}`,error.stack);
      throw new Error(`Error fetching application: ${error.message}`);
    }
  }

  async getAllApplicationsByPositionId(
    positionId: number,
    page: number = 0,
    pageSize: number = 10,
  ): Promise<ServicePaginatedApplicationsDTO> {
    const url = `${this.backendUrl}/applications?positionId=${positionId}&page=${page}&pageSize=${pageSize}`;
    this.logger.debug(`[Applications Service] Trying to fetch applications from: ${url}`);

    try {
      const response = await firstValueFrom(
        this.httpService.get<ServicePaginatedApplicationsDTO>(url),
      );
      this.logger.debug(`[Applications Service] Received response: ${JSON.stringify(response.data)}`);

      return response.data;
    } catch (error) {
      if (error.isAxiosError && error.code === 'ECONNREFUSED') {
        throw new ApplicationsBackendNotAvailableException();
      }

      this.logger.error(`Error fetching applications: ${error.message}`,error.stack);
      throw new Error(`Error fetching applications: ${error.message}`);
    }
  }

  async getApplicationCV(applicationId: string): Promise<{ data: any; headers: any }> {
    const url = `${this.backendUrl}/applications/${applicationId}/cv`;
    this.logger.debug(`[Applications Service] Trying to fetch CV from: ${url}`);

    try {
      const response = await firstValueFrom(
        this.httpService.get(url, { responseType: 'arraybuffer' }),
      );
      this.logger.debug(`[Applications Service] Received CV file with content type: ${response.headers['content-type']}`);

      return {
        data: response.data,
        headers: response.headers
      };
    } catch (error) {
      if (error.isAxiosError && error.response?.status === 404) {
        throw new NotFoundException(`CV for application with id ${applicationId} not found`);
      }

      if (error.isAxiosError && error.code === 'ECONNREFUSED') {
        throw new ApplicationsBackendNotAvailableException();
      }

      this.logger.error(`Error fetching CV: ${error.message}`, error.stack);
      throw new Error(`Error fetching CV: ${error.message}`);
    }
  }
}