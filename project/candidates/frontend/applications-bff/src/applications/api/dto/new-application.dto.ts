import { IsEmail, IsNotEmpty, isPhoneNumber, IsPositive } from 'class-validator';

export class CandidateDataDTO {
  @IsNotEmpty()
  name: string;

  @IsNotEmpty()
  @IsEmail()
  email: string;

  @IsNotEmpty()
  phone: string;
}

export class NewApplicationDTO {
  @IsPositive()
  positionId: number;

  @IsNotEmpty()
  candidate: CandidateDataDTO;

  constructor(positionId: number, candidate: CandidateDataDTO) {
    this.positionId = positionId;
    this.candidate = candidate;
  }
}

export class NewApplicationResponseDTO {
  applicationId: string;

  constructor(applicationId: string) {
    this.applicationId = applicationId;
  }
}