import { ConfigService } from '@nestjs/config';
import { Injectable } from '@nestjs/common';

@Injectable()
export class Config {
  private positionsBackendUrl: string;
  private insightsBackendUrl: string;
  private applicationsBackendUrl: string;

  constructor(private readonly configService: ConfigService) {
    this.positionsBackendUrl = this.configService.get<string>('POSITIONS_SERVICE_URL','http://localhost:9080');
    this.applicationsBackendUrl = this.configService.get<string>('APPLICATIONS_SERVICE_URL','http://localhost:8000');
    this.insightsBackendUrl = this.configService.get<string>('INSIGHTS_SERVICE_URL', 'http://localhost:9090');
  }

  getPositionsBackendUrl(): string {
    return this.positionsBackendUrl;
  }

  getInsightsBackendUrl(): string {
    return this.insightsBackendUrl;
  }

  getApplicationsBackendUrl(): string {
    return this.applicationsBackendUrl;
  }
}