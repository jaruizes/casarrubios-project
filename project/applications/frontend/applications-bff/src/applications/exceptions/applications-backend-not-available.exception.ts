import { BaseException } from '../../shared/exceptions/base.exception';


export class ApplicationsBackendNotAvailableException extends BaseException {
  constructor() {
    super(2000, 'Applications backend is not available');
    this.logger.error('Error managing applications: backend service unavailable');
  }
}