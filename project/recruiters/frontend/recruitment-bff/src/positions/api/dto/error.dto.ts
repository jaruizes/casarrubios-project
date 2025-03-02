export class ErrorDTO {
  error: number;
  message: string;

  constructor(error: number, message: string) {
    this.error = error;
    this.message = message;
  }
}