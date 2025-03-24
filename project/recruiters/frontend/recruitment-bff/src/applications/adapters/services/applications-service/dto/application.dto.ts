interface CandidateDataDTO {
  name: string;
  email: string;
  phone: string;
}

interface ServiceScoringDTO {
  score: number;
  descScore: number;
  requirementScore: number;
  tasksScore: number;
  timeSpent: number;
  explanation: string;
}

export interface ServiceSkillDTO {
  skill: string;
  level: number;
}

interface ServiceAnalysisDTO {
  summary: string;
  strengths: string[];
  concerns: string[];
  hardSkills: ServiceSkillDTO[];
  softSkills: ServiceSkillDTO[];
  keyResponsibilities: string[];
  interviewQuestions: string[];
  totalYearsExperience: number
  averagePermanency: number
  tags: string[];
}

export interface ServiceApplicationDTO {
  applicationId: string;
  positionId: number;
  candidate: CandidateDataDTO;
  cvFile: string;
  creationDate: string;
  analysis?: ServiceAnalysisDTO;
  scoring?: ServiceScoringDTO;
}

export class ServicePaginatedApplicationsDTO {
  applications: ServiceApplicationDTO[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}