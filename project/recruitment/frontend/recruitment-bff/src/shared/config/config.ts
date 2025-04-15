import { ConfigService } from '@nestjs/config';
import { Injectable } from '@nestjs/common';

@Injectable()
export class Config {
  private positionsBackendUrl: string;
  private globalPositionsBackendUrl: string;
  private applicationsBackendUrl: string;

  constructor(private readonly configService: ConfigService) {
    this.positionsBackendUrl = this.configService.get<string>('POSITIONS_SERVICE_URL','http://localhost:9080');
    this.applicationsBackendUrl = this.configService.get<string>('APPLICATIONS_SERVICE_URL','http://localhost:8000');
    this.globalPositionsBackendUrl = this.configService.get<string>('GLOBAL_POSITION_SERVICE_URL', 'http://localhost:9090');
  }

  getPositionsBackendUrl(): string {
    return this.positionsBackendUrl;
  }

  getGlobalPositionBackendUrl(): string {
    return this.globalPositionsBackendUrl;
  }

  getApplicationsBackendUrl(): string {
    return this.applicationsBackendUrl;
  }
}