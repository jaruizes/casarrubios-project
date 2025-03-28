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
  scoring: number
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
  analysis: Analysis;
  cvFile: string;
  scoring: Scoring;
}

interface Scoring {
  score: number;
  descScore: number;
  requirementScore: number;
  tasksScore: number;
  timeSpent: number;
  explanation: string;
}

export interface Skill{
  skill: string;
  level: number;
}

interface Analysis {
  summary: string;
  strengths: string[];
  concerns: string[];
  hardSkills: Skill[];
  softSkills: Skill[];
  keyResponsibilities: string[];
  interviewQuestions: string[];
  totalYearsExperience: number
  averagePermanency: number
  tags: string[];
}
