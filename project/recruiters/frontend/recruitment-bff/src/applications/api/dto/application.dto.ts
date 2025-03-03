export interface CandidateDataDTO {
  name: string;
  email: string;
  phone: string;
  tags?: string;
  totalExperience?: number;
  currentRole?: string;
  summary?: string;
}

export interface PositionAppliedDTO {
  id: number;
  matchingPercentage: number;
}

export interface ApplicationDTO {
  id: number;
  positionId: number;
  candidate: string;
  tags?: string;
  cvFile: string;
  creationDate: string;
  positionsApplied: PositionAppliedDTO[];
}

interface PositionDTO {
  id: number;
  title: string;
  createdAt: string;
}

export interface ApplicationDetailDTO {
  id: number;
  position: PositionDTO;
  candidate: CandidateDataDTO;
  cvFile: string;
  creationDate: string;
  matchingPercentage: number;
  questions: string[];
  analysis: string;
}

export class PaginatedApplicationsDTO {
  applications: ApplicationDTO[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}