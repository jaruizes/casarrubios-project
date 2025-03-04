import {Tag} from "./position";

export interface PositionApplied {
  id: number;
  matchingPercentage: number;
}

export interface Application {
  id: string;
  shortId: string;
  candidate: string;
  creationDate: string;
  tags: string;
  cv: string;
  positionsApplied: PositionApplied[];
}

export interface  PaginatedApplications {
  applications: Application[];
  totalElements: number;
  totalPages: number;
  size: number;
  pageNumber: number;
}

interface Candidate {
  name: string;
  email: string;
  phone: string;
  tags: Tag[];
  totalExperience: number;
  currentRole: string;
  summary: string;
}

interface Position {
  id: number;
  title: string;
  createdAt: string;
}

export interface ApplicationDetail {
  id: string;
  shortId: string;
  position: Position;
  creationDate: string;
  candidate: Candidate;
  matchingPercentage: number;
  questions: string[];
  analysis: string;
  cvFile: string;
}
