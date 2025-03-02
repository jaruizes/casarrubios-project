import {
  Body,
  Controller,
  Get,
  HttpException,
  HttpStatus,
  Logger,
  Param,
  ParseIntPipe,
  Post,
  Put,
  Query,
} from '@nestjs/common';
import { PositionsService } from '../adapters/services/position-service/positions.service';
import { PositionDTO, PositionsDTO } from './dto/positions.dto';
import { PositionDetailDTO, TagDTO } from './dto/position-detail.dto';
import { PositionServiceDTO } from '../adapters/services/position-service/dto/service-positions.dto';
import { PositionsBackendNotAvailableException } from '../model/exceptions/positions-backend-not-available.exception';
import { ErrorDTO } from './dto/error.dto';
import { PositionNotFoundException } from '../model/exceptions/position-not-found.exception';
import { NewPositionDTO } from './dto/new-position.dto';

@Controller('positions')
export class PositionsController {
  private readonly logger = new Logger(PositionsService.name);

  constructor(private readonly positionsService: PositionsService) {}

  @Get(':id')
  async getPositionById(
    @Param('id', ParseIntPipe) id: number,
  ): Promise<PositionDetailDTO | undefined> {
    this.logger.log(`Trying to fetch position with id: ${id}`);

    try {
      const positionServiceDTO =
        await this.positionsService.getPositionById(id);
      this.logger.log(`Received position with id: ${id}`);
      if (positionServiceDTO) {
        return this.toPositionDetailDTO(positionServiceDTO);
      }
    } catch (error) {
      if (error instanceof PositionsBackendNotAvailableException) {
        this.logger.log(
          `Error fetching position with id (${id}). Code: ${error.code}, Message: ${error.message}`,
        );
        throw new HttpException(
          new ErrorDTO(error.code, error.message),
          HttpStatus.SERVICE_UNAVAILABLE,
        );
      }

      if (error instanceof PositionNotFoundException) {
        this.logger.log(
          `Error fetching position with id (${id}). Message: ${error.message}`,
        );
        throw new HttpException(
          new ErrorDTO(1002, error.message),
          HttpStatus.NOT_FOUND,
        );
      }

      this.logger.error(
        `Error fetching position with id ${id}: ${error.message}`,
        error.stack,
      );
    }
  }

  @Get()
  async getAllPositions(
    @Query('status') status?: number,
    @Query('page') page: number = 0,
    @Query('limit') limit: number = 10,
  ): Promise<PositionsDTO> {
    this.logger.log(
      `Trying to fetch positions with status: ${status}, page: ${page}, limit: ${limit}`,
    );

    const paginatedPositions = await this.positionsService.getAllPositions(
      page,
      limit,
    );
    this.logger.log(`Received ${paginatedPositions.totalElements} positions`);

    return {
      positions: paginatedPositions.content.map((positionServiceDTO) =>
        this.toPositionDTO(positionServiceDTO),
      ),
      totalElements: paginatedPositions.totalElements,
      totalPages: paginatedPositions.totalPages,
      size: paginatedPositions.size,
      number: paginatedPositions.number,
    };
  }

  @Post()
  async createPosition(
    @Body() newPosition: NewPositionDTO,
  ): Promise<PositionDetailDTO | undefined> {
    this.logger.log(`Trying to create a new position`);

    const positionServiceDTO =
      await this.positionsService.createPosition(newPosition);
    if (positionServiceDTO) {
      this.logger.log(`Created position with id: ${positionServiceDTO.id}`);
    }

    return positionServiceDTO
      ? this.toPositionDetailDTO(positionServiceDTO)
      : undefined;
  }

  @Put(':id')
  async updatePosition(
    @Param('id', ParseIntPipe) id: number,
    @Body() newPosition: NewPositionDTO,
  ): Promise<PositionDetailDTO | undefined> {
    this.logger.log(`Trying to update position with id: ${id}`);

    const positionServiceDTO = await this.positionsService.updatePosition(
      id,
      newPosition,
    );
    this.logger.log(`Updated position with id: ${id}`);

    return positionServiceDTO
      ? this.toPositionDetailDTO(positionServiceDTO)
      : undefined;
  }

  private toPositionDTO(positionServiceDTO: PositionServiceDTO): PositionDTO {
    return {
      id: positionServiceDTO.id,
      title: positionServiceDTO.title,
      description: positionServiceDTO.description,
      status: positionServiceDTO.status,
      applications: positionServiceDTO.applications,
      creationDate: positionServiceDTO.createdAt,
      tags: this.buildTags(positionServiceDTO.tags),
    };
  }

  private toPositionDetailDTO(
    positionServiceDTO: PositionServiceDTO,
  ): PositionDetailDTO {
    return {
      id: positionServiceDTO.id,
      title: positionServiceDTO.title,
      description: positionServiceDTO.description,
      tags: this.buildTags(positionServiceDTO.tags),
      status: positionServiceDTO.status,
      applications: positionServiceDTO.applications,
      creationDate: positionServiceDTO.createdAt,
      requirements: positionServiceDTO.requirements,
      benefits: positionServiceDTO.benefits,
      tasks: positionServiceDTO.tasks,
    };
  }

  private buildTags(tags: string): TagDTO[] {
    if (tags) {
      return tags.split(',').map((tag) => {
        return { name: tag };
      });
    }

    return [];
  }
}
