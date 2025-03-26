import { Injectable, Logger } from '@nestjs/common';
import { firstValueFrom } from 'rxjs';
import { HttpService } from '@nestjs/axios';
import { AxiosResponse } from 'axios';
import { Config } from '../../../../shared/config/config';
import { ServiceGlobalPositionDTO } from './dto/service-global-position.dto';
import { GlobalPositionBackendNotAvailableException } from '../../../model/exceptions/global-position-backend-not-available.exception';

@Injectable()
export class GlobalPositionService {
  private backendUrl: string;
  private readonly logger = new Logger(GlobalPositionService.name);

  constructor(private httpService: HttpService,private readonly config: Config) {
    this.backendUrl = this.config.getGlobalPositionBackendUrl();
    this.logger.log(`Backend URL set to: ${this.backendUrl}`);
  }

  async getGlobalPosition(): Promise<ServiceGlobalPositionDTO> {
    const url = `${this.backendUrl}/global-position`;
    this.logger.debug(`[SERVICE] Trying to get global position from: ${url}`);

    try {
      const response: AxiosResponse<ServiceGlobalPositionDTO> = await firstValueFrom(this.httpService.get(url));
      this.logger.debug(`[SERVICE] Received response: ${JSON.stringify(response.data)}`);

      return response.data;
    } catch (error) {
      if (error.isAxiosError && error.code === 'ECONNREFUSED') {
        throw new GlobalPositionBackendNotAvailableException();
      }

      this.logger.error(`Error getting global position: ${error.message}`,error.stack);
      throw new Error(`Error getting global position: ${error.message}`);
    }
  }
}
