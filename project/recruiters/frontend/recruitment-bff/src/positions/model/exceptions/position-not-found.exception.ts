import { BaseException } from '../../../shared/exceptions/base.exception';

export class PositionNotFoundException extends BaseException {
  constructor(positionId: number) {
    super(1000, `Position with id ${positionId} not found`);
    this.logger.error(`Position with id ${positionId} not found`);
  }
}