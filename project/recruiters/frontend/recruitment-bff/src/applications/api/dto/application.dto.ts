export interface CandidateDataDTO {
  name: string;
  email: string;
  phone: string;
}

export interface ApplicationDTO {
  positionId: number;
  candidate: CandidateDataDTO;
  cvFile: string;
}

export class PaginatedApplicationsDTO {
  applications: ApplicationDTO[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}