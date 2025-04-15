export interface TagDTO {
  name: string;
}

export interface RequirementDTO {
  key: string;
  value: string;
  description: string;
  isMandatory: boolean;
}

export interface TaskDTO {
  description: string;
}

export interface BenefitDTO {
  description: string;
}

export class PositionDetailDTO {
  id: number;
  title: string;
  description: string;
  tags: TagDTO[];
  status: number;
  applications: number;
  creationDate: string;
  requirements: RequirementDTO[];
  benefits: BenefitDTO[];
  tasks: TaskDTO[];
}