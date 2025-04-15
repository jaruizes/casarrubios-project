export interface TagDTO {
  name: string;
}

export interface PositionDTO {
  id: number;
  title: string;
  description: string;
  status: number;
  applications: number;
  creationDate: string;
  tags: TagDTO[];
}

export interface PositionsDTO {
  positions: PositionDTO[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}