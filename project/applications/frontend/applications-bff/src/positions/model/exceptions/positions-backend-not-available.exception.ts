import { BaseException } from './base.exception';

export class PositionsBackendNotAvailableException extends BaseException {
  constructor() {
    super(1000, 'Positions backend is not available');
    this.logger.error('Error fetching positions: backend service unavailable');
  }
}