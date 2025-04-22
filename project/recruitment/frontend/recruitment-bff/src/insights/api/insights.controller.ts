import {
  Controller,
  Get,
  HttpException,
  HttpStatus,
  Logger,
} from '@nestjs/common';
import { InsightsDto } from './dto/insights.dto';
import { InsightsService } from '../adapters/services/insights-service/insights.service';
import { ServiceInsightsDto } from '../adapters/services/insights-service/dto/service-insights.dto';
import { InsightsBackendNotAvailableException } from '../model/exceptions/insights-backend-not-available.exception';
import { ErrorDTO } from '../../shared/api/dto/error.dto';

@Controller('insights')
export class InsightsController {
  private readonly logger = new Logger(InsightsController.name);

  constructor(private readonly globalInsights: InsightsService) {}

  @Get()
  async getInsights(): Promise<InsightsDto | undefined> {
    this.logger.log(`Trying to fetch global position`);

    try {
      const serviceInsightsDTO =
        await this.globalInsights.getInsights();
      this.logger.log(`Received global position`);
      if (serviceInsightsDTO) {
        return this.toInsightsDTO(serviceInsightsDTO);
      }
    } catch (error) {
      if (error instanceof InsightsBackendNotAvailableException) {
        this.logger.log(
          `Error fetching global position. Code: ${error.code}, Message: ${error.message}`,
        );
        throw new HttpException(
          new ErrorDTO(error.code, error.message),
          HttpStatus.SERVICE_UNAVAILABLE,
        );
      }

      throw new HttpException(
        new ErrorDTO(2000, error.message),
        HttpStatus.SERVICE_UNAVAILABLE,
      );
    }
  }

  private toInsightsDTO(
    serviceInsightsDTO: ServiceInsightsDto,
  ): InsightsDto {
    return {
      totalPositions: serviceInsightsDTO.totalPositions,
      averageApplications: serviceInsightsDTO.averageApplications,
      averageScore: serviceInsightsDTO.averageScore,
    };
  }
}
