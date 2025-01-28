interface Tag {
  name: string;
}

interface Requirement {
  key: string;
  value: string;
  description: string;
}

interface Task {
  description: string;
}

interface Benefit {
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
