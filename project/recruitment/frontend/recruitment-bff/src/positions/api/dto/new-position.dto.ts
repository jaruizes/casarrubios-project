import {
  BenefitDTO,
  RequirementDTO,
  TagDTO,
  TaskDTO,
} from './position-detail.dto';

export class NewPositionDTO {
  title: string;
  description: string;
  tags: TagDTO[];
  requirements: RequirementDTO[];
  benefits: BenefitDTO[];
  tasks: TaskDTO[];
}