import { Injectable } from '@nestjs/common';
import { ApplicationDTO } from './application.dto';

@Injectable()
export class ApplicationService {
  createApplication(application:ApplicationDTO): string {
    return 'OK';
  }
}