export interface RequirementDTO {
  key: string;
  value: string;
  description: string;
  mandatory: boolean;
}

export interface TaskDTO {
  description: string;
}

export interface BenefitDTO {
  description: string;
}

export interface PositionServiceDTO {
  id: number;
  title: string;
  description: string;
  tags: string;
  status: number;
  applications: number;
  createdAt: string;
  requirements: RequirementDTO[];
  benefits: BenefitDTO[];
  tasks: TaskDTO[];
}

export interface PaginatedPositionsDTO {
  content: PositionServiceDTO[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}