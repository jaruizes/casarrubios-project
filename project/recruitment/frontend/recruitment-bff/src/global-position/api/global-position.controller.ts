import {
  Controller,
  Get,
  HttpException,
  HttpStatus,
  Logger,
} from '@nestjs/common';
import { GlobalPositionDTO } from './dto/global-position.dto';
import { GlobalPositionService } from '../adapters/services/global-position-service/global-position.service';
import { ServiceGlobalPositionDTO } from '../adapters/services/global-position-service/dto/service-global-position.dto';
import { GlobalPositionBackendNotAvailableException } from '../model/exceptions/global-position-backend-not-available.exception';
import { ErrorDTO } from '../../shared/api/dto/error.dto';

@Controller('global-position')
export class GlobalPositionController {
  private readonly logger = new Logger(GlobalPositionController.name);

  constructor(private readonly globalPositionService: GlobalPositionService) {}

  @Get()
  async getGlobalPosition(): Promise<GlobalPositionDTO | undefined> {
    this.logger.log(`Trying to fetch global position`);

    try {
      const serviceGlobalPositionDTO =
        await this.globalPositionService.getGlobalPosition();
      this.logger.log(`Received global position`);
      if (serviceGlobalPositionDTO) {
        return this.toGlobalPositionDTO(serviceGlobalPositionDTO);
      }
    } catch (error) {
      if (error instanceof GlobalPositionBackendNotAvailableException) {
        this.logger.log(
          `Error fetching global position. Code: ${error.code}, Message: ${error.message}`,
        );
        throw new HttpException(
          new ErrorDTO(error.code, error.message),
          HttpStatus.SERVICE_UNAVAILABLE,
        );
      }
    }
  }

  private toGlobalPositionDTO(
    serviceGlobalPositionDTO: ServiceGlobalPositionDTO,
  ): GlobalPositionDTO {
    return {
      totalPositions: serviceGlobalPositionDTO.totalPositions,
      averageApplications: serviceGlobalPositionDTO.averageApplications,
      averageScore: serviceGlobalPositionDTO.averageScore,
    };
  }
}
