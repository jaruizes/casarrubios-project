import { BenefitDTO, RequirementDTO, TaskDTO } from './service-positions.dto';

export class ServiceNewPositionDTO {
  title: string;
  description: string;
  tags: string;
  requirements: RequirementDTO[];
  benefits: BenefitDTO[];
  tasks: TaskDTO[];
}