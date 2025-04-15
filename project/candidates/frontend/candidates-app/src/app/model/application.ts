export interface CandidateData {
  name: string;
  email: string;
  phone: string;
}

export interface Application {
  positionId: number;
  candidate: CandidateData;
}
