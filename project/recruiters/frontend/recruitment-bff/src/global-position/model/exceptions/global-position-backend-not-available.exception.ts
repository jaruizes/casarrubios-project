import { BaseException } from '../../../shared/exceptions/base.exception';

export class GlobalPositionBackendNotAvailableException extends BaseException {
  constructor() {
    super(5000, 'Global Position backend is not available');
    this.logger.error(
      'Error getting global position: backend service unavailable',
    );
  }
}