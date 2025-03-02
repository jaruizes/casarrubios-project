interface CandidateDataDTO {
  name: string;
  email: string;
  phone: string;
}

export interface ServiceApplicationDTO {
  positionId: number;
  candidate: CandidateDataDTO;
  cvFile: string;
}

export class ServicePaginatedApplicationsDTO {
  applications: ServiceApplicationDTO[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}