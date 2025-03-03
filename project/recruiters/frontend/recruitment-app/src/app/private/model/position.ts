export interface Tag {
  name: string;
}

export interface Requirement {
  key: string;
  value: string;
  description: string;
  isMandatory: boolean;
}

export interface Task {
  description: string;
}

export interface Benefit {
  description: string;
}

export interface Position {
  id: number;
  title: string;
  description: string;
  tags: Tag[];
  applications: number;
  status: number;
  creationDate: string;
  requirements: Requirement[];
  tasks: Task[];
  benefits: Benefit[];
}


export interface PaginatedPositions {
  positions: Position[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}
