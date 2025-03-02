import {
  Controller,
  Get,
  HttpException,
  HttpStatus,
  Logger,
  NotFoundException,
  Param,
  ParseIntPipe,
  Query,
} from '@nestjs/common';
import {
  ApplicationDTO,
  PaginatedApplicationsDTO,
} from './dto/application.dto';
import { ApplicationsService } from '../adapters/services/applications-service/applications.service';
import { ErrorDTO } from '../../positions/api/dto/error.dto';
import { ApplicationsBackendNotAvailableException } from '../exceptions/applications-backend-not-available.exception';
import {
  ServiceApplicationDTO,
  ServicePaginatedApplicationsDTO,
} from '../adapters/services/applications-service/dto/application.dto';

@Controller('applications')
export class ApplicationsController {
  private readonly logger = new Logger(ApplicationsController.name);

  constructor(private readonly applicationsService: ApplicationsService) {}

  @Get(':applicationId')
  async getApplicationById(
    @Param('applicationId', ParseIntPipe) applicationId: number,
  ): Promise<ApplicationDTO | undefined> {
    this.logger.log(`Trying to fetch application with id: ${applicationId}`);

    try {
      const applicationServiceDTO: ServiceApplicationDTO | undefined =
        await this.applicationsService.getApplicationById(applicationId);
      this.logger.log(`Received position with id: ${applicationId}`);
      if (applicationServiceDTO) {
        return this.toApplicationDTO(applicationServiceDTO);
      }
    } catch (error) {
      if (error instanceof ApplicationsBackendNotAvailableException) {
        this.logger.log(
          `Error fetching position with id (${applicationId}). Code: ${error.code}, Message: ${error.message}`,
        );
        throw new HttpException(
          new ErrorDTO(error.code, error.message),
          HttpStatus.SERVICE_UNAVAILABLE,
        );
      }

      if (error instanceof NotFoundException) {
        this.logger.log(
          `Error fetching position with id (${applicationId}). Message: ${error.message}`,
        );
        throw new HttpException(
          new ErrorDTO(1002, error.message),
          HttpStatus.NOT_FOUND,
        );
      }

      this.logger.error(
        `Error fetching position with id ${applicationId}: ${error.message}`,
        error.stack,
      );
    }
  }

  @Get()
  async getAllPositionsByPositionId(
    @Query('positionId') positionId: number,
    @Query('page') page: number = 0,
    @Query('pageSize') pageSize: number = 10,
  ): Promise<PaginatedApplicationsDTO> {
    this.logger.log(
      `Trying to fetch application with positionId: ${positionId}, page: ${page}, pageSize: ${pageSize}`,
    );

    const paginatedPositions =
      await this.applicationsService.getAllApplicationsByPositionId(
        positionId,
        page,
        pageSize,
      );
    this.logger.log(
      `Found ${paginatedPositions.totalElements} applications. Returning page ${paginatedPositions.number} of ${paginatedPositions.totalPages}`,
    );

    return {
      applications: paginatedPositions.applications.map(
        (applicationServiceDTO) => this.toApplicationDTO(applicationServiceDTO),
      ),
      totalElements: paginatedPositions.totalElements,
      totalPages: paginatedPositions.totalPages,
      size: paginatedPositions.size,
      number: paginatedPositions.number,
    };
  }

  private toPaginatedApplicationsDTO(
    servicePaginatedApplicationsDTO: ServicePaginatedApplicationsDTO,
  ): PaginatedApplicationsDTO {
    return {
      applications: servicePaginatedApplicationsDTO.applications.map(
        (serviceApplicationDTO) => this.toApplicationDTO(serviceApplicationDTO),
      ),
      totalElements: servicePaginatedApplicationsDTO.totalElements,
      totalPages: servicePaginatedApplicationsDTO.totalPages,
      size: servicePaginatedApplicationsDTO.size,
      number: servicePaginatedApplicationsDTO.number,
    };
  }

  private toApplicationDTO(
    serviceApplicationDTO: ServiceApplicationDTO,
  ): ApplicationDTO {
    return {
      positionId: serviceApplicationDTO.positionId,
      candidate: serviceApplicationDTO.candidate,
      cvFile: serviceApplicationDTO.cvFile,
    };
  }
}