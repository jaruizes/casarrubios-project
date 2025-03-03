interface CandidateDataDTO {
  name: string;
  email: string;
  phone: string;
}

export interface ServiceApplicationDTO {
  applicationId: number;
  positionId: number;
  candidate: CandidateDataDTO;
  cvFile: string;
  creationDate: string;
}

export class ServicePaginatedApplicationsDTO {
  applications: ServiceApplicationDTO[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}