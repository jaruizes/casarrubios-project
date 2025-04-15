export interface CandidateDTO {
  name: string;
  email: string;
  phone: string;
}

export interface ApplicationDTO {
  candidate: CandidateDTO;
  positionId: number;
  cvFile: string;
}

export interface ApplicationResponseDTO {
  position: number;
  applicationId: string;
}