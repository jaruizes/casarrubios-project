import {
  Controller,
  Get,
  HttpException,
  HttpStatus,
  Logger,
  NotFoundException,
  Param,
  Query,
  ParseIntPipe,
  Res,
  StreamableFile,
} from '@nestjs/common';
import {
  ApplicationDetailDTO,
  ApplicationDTO,
  CandidateDataDTO,
  PaginatedApplicationsDTO,
  PositionAppliedDTO,
} from './dto/application.dto';
import { ApplicationsService } from '../adapters/services/applications-service/applications.service';
import { ErrorDTO } from '../../positions/api/dto/error.dto';
import { ApplicationsBackendNotAvailableException } from '../exceptions/applications-backend-not-available.exception';
import { ServiceApplicationDTO } from '../adapters/services/applications-service/dto/application.dto';
import { PositionsService } from '../../positions/adapters/services/position-service/positions.service';
import { PositionServiceDTO } from '../../positions/adapters/services/position-service/dto/service-positions.dto';
import * as crypto from 'crypto';
import { Response } from 'express';

@Controller('applications')
export class ApplicationsController {
  private readonly logger = new Logger(ApplicationsController.name);

  constructor(
    private readonly applicationsService: ApplicationsService,
    private readonly positionsService: PositionsService,
  ) {}

  @Get(':applicationId')
  async getApplicationById(@Param('applicationId') applicationId: string): Promise<ApplicationDetailDTO | undefined> {
    this.logger.log(`Trying to fetch application detail with id: ${applicationId}`);

    try {
      const applicationServiceDTO: ServiceApplicationDTO | undefined = await this.applicationsService.getApplicationById(applicationId);
      const position: PositionServiceDTO = await this.positionsService.getPositionById(applicationServiceDTO.positionId);

      if (applicationServiceDTO && position) {
        this.logger.log(`Found application with id: ${applicationId}`);
        return this.toApplicationDetailDTO(applicationServiceDTO, position);
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

      // TODO: PositionNotFoundException && PositionBackendNotAvailableException

      this.logger.error(
        `Error fetching position with id ${applicationId}: ${error.message}`,
        error.stack,
      );
    }
  }

  @Get(':applicationId/cv')
  async getApplicationCV(
    @Param('applicationId') applicationId: string,
    @Res({ passthrough: true }) res: Response,
  ) {
    this.logger.log(`Trying to fetch CV for application with id: ${applicationId}`);

    try {
      const cvResponse = await this.applicationsService.getApplicationCV(applicationId);
      
      // Set the appropriate headers
      res.set({
        'Content-Type': cvResponse.headers['content-type'] || 'application/pdf',
        'Content-Disposition': cvResponse.headers['content-disposition'] || `attachment; filename="cv-${applicationId}.pdf"`,
      });

      // Return the file as a StreamableFile
      return new StreamableFile(cvResponse.data);
    } catch (error) {
      if (error instanceof ApplicationsBackendNotAvailableException) {
        this.logger.log(
          `Error fetching CV for application with id (${applicationId}). Code: ${error.code}, Message: ${error.message}`,
        );
        throw new HttpException(
          new ErrorDTO(error.code, error.message),
          HttpStatus.SERVICE_UNAVAILABLE,
        );
      }

      if (error instanceof NotFoundException) {
        this.logger.log(
          `Error fetching CV for application with id (${applicationId}). Message: ${error.message}`,
        );
        throw new HttpException(
          new ErrorDTO(1002, error.message),
          HttpStatus.NOT_FOUND,
        );
      }

      this.logger.error(
        `Error fetching CV for application with id ${applicationId}: ${error.message}`,
        error.stack,
      );
      throw new HttpException(
        new ErrorDTO(1001, `Error fetching CV: ${error.message}`),
        HttpStatus.INTERNAL_SERVER_ERROR,
      );
    }
  }

  @Get()
  async getAllPositionsByPositionId(
    @Query('positionId') positionId: number,
    @Query('page') page: number = 0,
    @Query('pageSize') pageSize: number = 10,
  ): Promise<PaginatedApplicationsDTO> {
    this.logger.log(`Trying to fetch application with positionId: ${positionId}, page: ${page}, pageSize: ${pageSize}`);

    const paginatedPositions =
      await this.applicationsService.getAllApplicationsByPositionId(positionId, page, pageSize);
    this.logger.log(`Found ${paginatedPositions.totalElements} applications. Returning page ${paginatedPositions.number} of ${paginatedPositions.totalPages}`);

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

  private toApplicationDTO(serviceApplicationDTO: ServiceApplicationDTO): ApplicationDTO {
    const positionsApplied: PositionAppliedDTO[] = [
      {
        id: serviceApplicationDTO.positionId,
        matchingPercentage: 0,
      },
    ];

    return {
      id: serviceApplicationDTO.applicationId,
      shortId: this.generateShortId(serviceApplicationDTO.applicationId),
      positionId: serviceApplicationDTO.positionId,
      candidate: serviceApplicationDTO.candidate.name,
      cvFile: serviceApplicationDTO.cvFile,
      creationDate: serviceApplicationDTO.creationDate,
      positionsApplied: positionsApplied,
      scoring: serviceApplicationDTO.scoring ? serviceApplicationDTO.scoring.score : undefined,
      tags: serviceApplicationDTO.analysis ? serviceApplicationDTO.analysis.tags.join(', ') : ''
    };
  }

  private toApplicationDetailDTO(applicationDTO: ServiceApplicationDTO, positionDTO: PositionServiceDTO): ApplicationDetailDTO {
    const candidateDataDTO: CandidateDataDTO = {
      name: applicationDTO.candidate.name,
      email: applicationDTO.candidate.email,
      phone: applicationDTO.candidate.phone
    };



    return {
      id: applicationDTO.applicationId,
      shortId: this.generateShortId(applicationDTO.applicationId),
      position: {
        id: positionDTO.id,
        title: positionDTO.title,
        createdAt: positionDTO.createdAt,
      },
      candidate: candidateDataDTO,
      cvFile: applicationDTO.cvFile,
      creationDate: applicationDTO.creationDate,
      analysis: applicationDTO.analysis ? applicationDTO.analysis : undefined,
      scoring: applicationDTO.scoring ? applicationDTO.scoring : undefined,
    };
  }

  private generateShortId(uuid: string): string {
    return crypto.createHash('md5').update(uuid).digest('hex').substring(0, 6);
  }
}
