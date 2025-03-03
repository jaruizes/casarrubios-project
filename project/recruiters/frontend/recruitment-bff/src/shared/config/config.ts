import { ConfigService } from '@nestjs/config';
import { Injectable } from '@nestjs/common';

@Injectable()
export class Config {
  private positionsBackendUrl: string;
  private applicationsBackendUrl: string;

  constructor(private readonly configService: ConfigService) {
    this.positionsBackendUrl = this.configService.get<string>(
      'POSITIONS_SERVICE_URL',
      'http://localhost:9080',
    );
    this.applicationsBackendUrl = this.configService.get<string>(
      'APPLICATIONS_SERVICE_URL',
      'http://localhost:9081',
    );
  }

  getPositionsBackendUrl(): string {
    return this.positionsBackendUrl;
  }

  getApplicationsBackendUrl(): string {
    return this.applicationsBackendUrl;
  }
}