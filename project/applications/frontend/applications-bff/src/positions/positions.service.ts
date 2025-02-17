import { Injectable, NotFoundException } from '@nestjs/common';
import { Position } from './position.dto';

const mockData: Position[] = [
  {
      id: 1,
      title: 'Frontend Developer',
      description: 'Develop user interfaces with Angular',
      tags: [{ name: 'Angular' }, { name: 'TypeScript' }],
      status: 1,
      requirements: [
        { key: 'Experience', value: '2 years', description: 'Frontend experience', isMandatory: true },
        { key: 'Skill', value: 'Angular', description: 'Proficiency in Angular framework', isMandatory: false },
      ],
      tasks: [{ description: 'Develop reusable components' }],
      benefits: [{ description: 'Flexible hours' }, { description: 'Remote work' }],
      applications: 10,
      creationDate: '2025-01-22T10:23:08.350Z',
    },
    {
      id: 2,
      title: 'Backend Developer',
      description: 'Build robust APIs with Node.js',
      tags: [{ name: 'Node.js' }, { name: 'Express' }],
      status: 2,
      requirements: [
        { key: 'Experience', value: '3 years', description: 'Backend experience', isMandatory: true },
        { key: 'Skill', value: 'Node.js', description: 'Proficiency in Node.js', isMandatory: true },
      ],
      tasks: [{ description: 'Design RESTful APIs' }],
      benefits: [{ description: 'Health insurance' }, { description: 'Stock options' }],
      applications: 5,
      creationDate: '2025-01-24T10:23:08.350Z',
    },
    {
      id: 3,
      title: 'Data Scientist',
      description: 'Analyze data and build predictive models',
      tags: [{ name: 'Python' }, { name: 'Machine Learning' }],
      status: 1,
      requirements: [
        { key: 'Experience', value: '2 years', description: 'Data analysis experience', isMandatory: true },
        { key: 'Skill', value: 'Python', description: 'Proficiency in Python', isMandatory: true },
      ],
      tasks: [{ description: 'Build predictive models' }],
      benefits: [{ description: 'Flexible hours' }, { description: 'Training budget' }],
      applications: 12,
      creationDate: '2025-01-26T10:23:08.350Z',
    },
    {
      id: 4,
      title: 'DevOps Engineer',
      description: 'Manage infrastructure and deployment pipelines',
      tags: [{ name: 'AWS' }, { name: 'Terraform' }],
      status: 1,
      requirements: [
        { key: 'Experience', value: '3 years', description: 'DevOps experience', isMandatory: true },
        { key: 'Skill', value: 'Terraform', description: 'Experience with Terraform and AWS', isMandatory: true },
      ],
      tasks: [{ description: 'Automate CI/CD pipelines' }],
      benefits: [{ description: 'Stock options' }, { description: 'Remote work' }],
      applications: 8,
      creationDate: '2025-01-27T10:23:08.350Z',
    },
    {
      id: 5,
      title: 'Product Manager',
      description: 'Lead product development and strategy',
      tags: [{ name: 'Leadership' }, { name: 'Product Management' }],
      status: 2,
      requirements: [
        { key: 'Experience', value: '5 years', description: 'Product management experience', isMandatory: true },
        { key: 'Skill', value: 'Leadership', description: 'Strong leadership skills', isMandatory: true },
      ],
      tasks: [{ description: 'Define product roadmap' }],
      benefits: [{ description: 'Company car' }, { description: 'Health insurance' }],
      applications: 15,
      creationDate: '2025-01-29T10:23:08.350Z',
    }
  ];



@Injectable()
export class PositionsService {

  getAllPositions(status?: number, page: number = 1, limit: number = 10): Position[] {
    let filteredPositions = mockData;

    if (status !== undefined) {
      filteredPositions = filteredPositions.filter(p => p.status === status);
    }

    return filteredPositions.slice((page - 1) * limit, page * limit);
  }

  getPositionById(id: number): Position | undefined {
    let position = mockData.find(p => p.id === id);
    if (!position) {
      throw new NotFoundException(`Position with id ${id} not found`);
    }

    return position;
  }
}