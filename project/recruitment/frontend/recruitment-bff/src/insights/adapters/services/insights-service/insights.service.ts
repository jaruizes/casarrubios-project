import { Injectable, Logger } from '@nestjs/common';
import { firstValueFrom } from 'rxjs';
import { HttpService } from '@nestjs/axios';
import { AxiosResponse } from 'axios';
import { Config } from '../../../../shared/config/config';
import { ServiceInsightsDto } from './dto/service-insights.dto';
import { InsightsBackendNotAvailableException } from '../../../model/exceptions/insights-backend-not-available.exception';

@Injectable()
export class InsightsService {
  private backendUrl: string;
  private readonly logger = new Logger(InsightsService.name);

  constructor(private httpService: HttpService,private readonly config: Config) {
    this.backendUrl = this.config.getInsightsBackendUrl();
    this.logger.log(`Backend URL set to: ${this.backendUrl}`);
  }

  async getInsights(): Promise<ServiceInsightsDto> {
    const url = `${this.backendUrl}/insights`;
    this.logger.debug(`[SERVICE] Trying to get insights from: ${url}`);

    try {
      const response: AxiosResponse<ServiceInsightsDto> = await firstValueFrom(this.httpService.get(url));
      this.logger.debug(`[SERVICE] Received response: ${JSON.stringify(response.data)}`);

      return response.data;
    } catch (error) {
      if (error.isAxiosError && error.code === 'ECONNREFUSED') {
        throw new InsightsBackendNotAvailableException();
      }

      this.logger.error(`Error getting insights: ${error.message}`,error.stack);
      throw new Error(`Error getting insights: ${error.message}`);
    }
  }
}
