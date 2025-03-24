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
  id: string;
  shortId: string;
  positionId: number;
  candidate: string;
  cvFile: string;
  creationDate: string;
  positionsApplied: PositionAppliedDTO[];
  scoring?: number;
  tags: string;
}

interface PositionDTO {
  id: number;
  title: string;
  createdAt: string;
}

interface ScoringDTO {
  score: number;
  descScore: number;
  requirementScore: number;
  tasksScore: number;
  timeSpent: number;
  explanation: string;
}

export interface SkillDTO {
  skill: string;
  level: number;
}

interface AnalysisDTO {
  summary: string;
  strengths: string[];
  concerns: string[];
  hardSkills: SkillDTO[];
  softSkills: SkillDTO[];
  keyResponsibilities: string[];
  interviewQuestions: string[];
  totalYearsExperience: number
  averagePermanency: number
  tags: string[];
}

export interface ApplicationDetailDTO {
  id: string;
  shortId: string;
  position: PositionDTO;
  candidate: CandidateDataDTO;
  cvFile: string;
  creationDate: string;
  scoring?: ScoringDTO;
  analysis?: AnalysisDTO;
}

export class PaginatedApplicationsDTO {
  applications: ApplicationDTO[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}