import { BaseException } from '../../../shared/exceptions/base.exception';

export class InsightsBackendNotAvailableException extends BaseException {
  constructor() {
    super(5000, 'Insights backend is not available');
    this.logger.error(
      'Error getting insights: backend service unavailable',
    );
  }
}