import {Tag} from "./position";

export interface PositionApplied {
  id: number;
  matchingPercentage: number;
}

export interface Application {
  id: number;
  candidate: string;
  applicationDate: string;
  tags: Tag[];
  cv: string;
  positionsApplied: PositionApplied[];
}

interface Candidate {
  name: string;
  lastName: string;
  email: string;
  phone: string;
  tags: Tag[];
  totalExperience: number;
  currentRole: string;
  summary: string;
  cv: string;
}

interface Position {
  id: number;
  title: string;
  createdAt: string;
}

export interface ApplicationDetail {
  id: number;
  position: Position;
  applicationDate: string;
  candidate: Candidate;
  matchingPercentage: number;
  questions: string[];
  analysis: string;
}
