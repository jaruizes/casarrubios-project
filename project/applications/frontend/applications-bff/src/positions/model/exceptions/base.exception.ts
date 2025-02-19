import { Logger } from '@nestjs/common';

export class BaseException {
  code: number;
  message: string;

  protected readonly logger = new Logger(BaseException.name);

  constructor(code: number, message: string) {
    this.code = code;
    this.message = message;
  }
}