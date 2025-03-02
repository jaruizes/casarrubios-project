import { Injectable, Logger } from '@nestjs/common';
import { firstValueFrom } from 'rxjs';
import { HttpService } from '@nestjs/axios';
import { ConfigService } from '@nestjs/config';
import { AxiosError, AxiosResponse } from 'axios';
import {
  PaginatedPositionsDTO,
  PositionServiceDTO,
} from './dto/service-positions.dto';
import { PositionsBackendNotAvailableException } from '../../../model/exceptions/positions-backend-not-available.exception';
import { PositionNotFoundException } from '../../../model/exceptions/position-not-found.exception';
import { NewPositionDTO } from '../../../api/dto/new-position.dto';
import { ServiceNewPositionDTO } from './dto/service-new-position.dto';

@Injectable()
export class PositionsService {
  private backendUrl: string;
  private readonly logger = new Logger(PositionsService.name);

  constructor(
    private httpService: HttpService,
    private readonly configService: ConfigService,
  ) {
    this.backendUrl = this.configService.get<string>(
      'BACKEND_URL',
      'http://localhost:9080',
    );
    this.logger.log(`Backend URL set to: ${this.backendUrl}`);
  }

  async getAllPositions(
    page: number = 0,
    limit: number = 10,
  ): Promise<PaginatedPositionsDTO> {
    const url = `${this.backendUrl}/positions?page=${page}&limit=${limit}`;
    this.logger.debug(`[SERVICE] Trying to fetch positions from: ${url}`);

    try {
      const response: AxiosResponse<PaginatedPositionsDTO> =
        await firstValueFrom(this.httpService.get(url));
      this.logger.debug(
        `[SERVICE] Received response: ${JSON.stringify(response.data)}`,
      );

      return response.data;
    } catch (error) {
      if (error.isAxiosError && error.code === 'ECONNREFUSED') {
        throw new PositionsBackendNotAvailableException();
      }

      this.logger.error(
        `Error fetching positions: ${error.message}`,
        error.stack,
      );
      throw new Error(`Error fetching positions: ${error.message}`);
    }
  }

  async getPositionById(id: number): Promise<PositionServiceDTO | undefined> {
    const url = `${this.backendUrl}/positions/${id}`;
    this.logger.debug(`[SERVICE] Trying to fetch position from: ${url}`);

    try {
      const response: AxiosResponse<PositionServiceDTO> = await firstValueFrom(
        this.httpService.get(url),
      );
      this.logger.debug(
        `[SERVICE] Received response: ${JSON.stringify(response.data)}`,
      );

      return response.data;
    } catch (error) {
      this.manageError(error, id);
    }
  }

  async createPosition(
    newPosition: NewPositionDTO,
  ): Promise<PositionServiceDTO | undefined> {
    const url = `${this.backendUrl}/positions`;
    this.logger.debug(`[SERVICE] Trying to create new position to: ${url}`);
    this.logger.debug(`[SERVICE] New position: ${JSON.stringify(newPosition)}`);

    const serviceNewPositionDTO = this.toServiceNewPositionDTO(newPosition);
    this.logger.debug(
      `[SERVICE] Service new position: ${JSON.stringify(serviceNewPositionDTO)}`,
    );

    try {
      const response: AxiosResponse<PositionServiceDTO> = await firstValueFrom(
        this.httpService.post(url, serviceNewPositionDTO),
      );
      this.logger.debug(
        `[SERVICE] Received response: ${JSON.stringify(response.data)}`,
      );

      return response.data;
    } catch (error) {
      this.logger.error(
        `Error creating position: ${error.message}`,
        error.stack,
      );
      throw new Error(`Error creating position: ${error.message}`);
    }
  }

  async updatePosition(
    id: number,
    newPosition: NewPositionDTO,
  ): Promise<PositionServiceDTO | undefined> {
    const url = `${this.backendUrl}/positions/${id}`;
    this.logger.debug(`[SERVICE] Trying to update position to: ${url}`);
    this.logger.debug(`[SERVICE] Position: ${JSON.stringify(newPosition)}`);

    const positionToUpdate = this.toServiceNewPositionDTO(newPosition);
    this.logger.debug(
      `[SERVICE] Service position: ${JSON.stringify(positionToUpdate)}`,
    );

    try {
      const response: AxiosResponse<PositionServiceDTO> = await firstValueFrom(
        this.httpService.put(url, positionToUpdate),
      );
      this.logger.debug(
        `[SERVICE] Received response: ${JSON.stringify(response.data)}`,
      );

      return response.data;
    } catch (error) {
      this.manageError(error, id);
    }
  }

  private manageError(error: any, id: number): void {
    if (error.isAxiosError) {
      const axiosError = error as AxiosError;
      if (axiosError.response?.status === 404) {
        throw new PositionNotFoundException(id);
      }

      if (axiosError.code === 'ECONNREFUSED') {
        throw new PositionsBackendNotAvailableException();
      }
    }

    this.logger.error(
      `Error fetching position with id ${id}: ${error.message}`,
    );
    throw new Error(`Error fetching position with id ${id}: ${error.message}`);
  }

  private toServiceNewPositionDTO(
    newPosition: NewPositionDTO,
  ): ServiceNewPositionDTO {
    const tags: string = newPosition.tags.map((tag) => tag.name).join(', ');
    return {
      title: newPosition.title,
      description: newPosition.description,
      tags: tags,
      requirements: newPosition.requirements,
      tasks: newPosition.tasks,
      benefits: newPosition.benefits,
    };
  }
}