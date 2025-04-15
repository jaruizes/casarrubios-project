import { Test, TestingModule } from '@nestjs/testing';
import { PositionsController } from './positions.controller';
import { HttpException, HttpStatus } from '@nestjs/common';
import { PositionsService } from '../adapters/services/position-service/positions.service';
import { PositionDTO, TagDTO } from './dto/positions.dto';
import { PositionDetailDTO } from './dto/position-detail.dto';
import {
  PaginatedPositionsDTO,
  PositionServiceDTO,
} from '../adapters/services/position-service/dto/service-positions.dto';
import { PositionNotFoundException } from '../model/exceptions/position-not-found.exception';
import { ErrorDTO } from '../../shared/api/dto/error.dto';
import { PositionsBackendNotAvailableException } from '../model/exceptions/positions-backend-not-available.exception';

const mockData: PositionServiceDTO[] = [
  {
    id: 1,
    title: 'Frontend Developer',
    description: 'Develop user interfaces with Angular',
    tags: 'Java, Angular',
    status: 1,
    requirements: [
      {
        key: 'Experience',
        value: '2 years',
        description: 'Frontend experience',
        isMandatory: true,
      },
      {
        key: 'Skill',
        value: 'Angular',
        description: 'Proficiency in Angular framework',
        isMandatory: false,
      },
    ],
    tasks: [{ description: 'Develop reusable components' }],
    benefits: [
      { description: 'Flexible hours' },
      { description: 'Remote work' },
    ],
    applications: 10,
    creationDate: '2025-01-22T10:23:08.350Z',
  },
  {
    id: 2,
    title: 'Backend Developer',
    description: 'Build robust APIs with Node.js',
    tags: 'Java, Angular',
    status: 2,
    requirements: [
      {
        key: 'Experience',
        value: '3 years',
        description: 'Backend experience',
        isMandatory: true,
      },
      {
        key: 'Skill',
        value: 'Node.js',
        description: 'Proficiency in Node.js',
        isMandatory: true,
      },
    ],
    tasks: [{ description: 'Design RESTful APIs' }],
    benefits: [
      { description: 'Health insurance' },
      { description: 'Stock options' },
    ],
    applications: 5,
    creationDate: '2025-01-24T10:23:08.350Z',
  },
  {
    id: 3,
    title: 'Data Scientist',
    description: 'Analyze data and build predictive models',
    tags: 'Java, Angular',
    status: 1,
    requirements: [
      {
        key: 'Experience',
        value: '2 years',
        description: 'Data analysis experience',
        isMandatory: true,
      },
      {
        key: 'Skill',
        value: 'Python',
        description: 'Proficiency in Python',
        isMandatory: true,
      },
    ],
    tasks: [{ description: 'Build predictive models' }],
    benefits: [
      { description: 'Flexible hours' },
      { description: 'Training budget' },
    ],
    applications: 12,
    creationDate: '2025-01-26T10:23:08.350Z',
  },
  {
    id: 4,
    title: 'DevOps Engineer',
    description: 'Manage infrastructure and deployment pipelines',
    tags: 'Java, Angular',
    status: 1,
    requirements: [
      {
        key: 'Experience',
        value: '3 years',
        description: 'DevOps experience',
        isMandatory: true,
      },
      {
        key: 'Skill',
        value: 'Terraform',
        description: 'Experience with Terraform and AWS',
        isMandatory: true,
      },
    ],
    tasks: [{ description: 'Automate CI/CD pipelines' }],
    benefits: [
      { description: 'Stock options' },
      { description: 'Remote work' },
    ],
    applications: 8,
    creationDate: '2025-01-27T10:23:08.350Z',
  },
  {
    id: 5,
    title: 'Product Manager',
    description: 'Lead product development and strategy',
    tags: 'Java, Angular',
    status: 2,
    requirements: [
      {
        key: 'Experience',
        value: '5 years',
        description: 'Product management experience',
        isMandatory: true,
      },
      {
        key: 'Skill',
        value: 'Leadership',
        description: 'Strong leadership skills',
        isMandatory: true,
      },
    ],
    tasks: [{ description: 'Define product roadmap' }],
    benefits: [
      { description: 'Company car' },
      { description: 'Health insurance' },
    ],
    applications: 15,
    creationDate: '2025-01-29T10:23:08.350Z',
  },
];

describe('PositionController', () => {
  let positionController: PositionsController;
  let positionsService: jest.Mocked<PositionsService>;

  beforeEach(async () => {
    const mockPositionsService = {
      getAllPositions: jest.fn(),
      getPositionById: jest.fn(),
    };

    const app: TestingModule = await Test.createTestingModule({
      controllers: [PositionsController],
      providers: [
        { provide: PositionsService, useValue: mockPositionsService },
      ],
    }).compile();

    positionController = app.get<PositionsController>(PositionsController);
    positionsService = app.get(PositionsService);
  });

  describe('getAllPositions', () => {
    it('given some positions created, it should return all positions', async () => {
      const result: PaginatedPositionsDTO = {
        content: mockData,
        totalElements: mockData.length,
        totalPages: 1,
        size: 5,
        number: 0,
      };

      positionsService.getAllPositions.mockResolvedValue(result);

      let positions = await positionController.getAllPositions();
      expect(positions).toHaveProperty('positions');
      expect(positions).toHaveProperty('totalElements');
      expect(positions).toHaveProperty('totalPages');
      expect(positions).toHaveProperty('size');
      expect(positions).toHaveProperty('number');

      expect(positions.positions).toHaveLength(5);
      positions.positions.forEach((position) => {
        checkPosition(position);
      });
    });

    it('given positions but no connection to backend, it should return 503 status', async () => {
      positionsService.getPositionById.mockRejectedValue(
        new PositionsBackendNotAvailableException(),
      );

      try {
        await positionController.getPositionById(99999);
      } catch (error) {
        expect(error).toBeInstanceOf(HttpException);
        expect(error.status).toEqual(HttpStatus.SERVICE_UNAVAILABLE);
        expect(error.response).toBeInstanceOf(ErrorDTO);
        expect(error.response).toHaveProperty('error');
        expect(error.response.error).toBe(1000);
        expect(error.response).toHaveProperty('message');
      }
    });
  });

  describe('getPositionById', () => {
    it('given a valid position id, it should return a position', async () => {
      const positionId = 1;
      const result = mockData.find((position) => position.id === positionId);
      positionsService.getPositionById.mockResolvedValue(result);

      let position = await positionController.getPositionById(positionId);
      expect(position).toBeDefined();
      checkPositionDetail(position, positionId);
    });

    it('given a not valid position id, it should return NotFoundException and 404 status', async () => {
      const positionId = 99999;
      positionsService.getPositionById.mockRejectedValue(
        new PositionNotFoundException(99999),
      );

      try {
        await positionController.getPositionById(99999);
      } catch (error) {
        expect(error).toBeInstanceOf(HttpException);
        expect(error.status).toEqual(HttpStatus.NOT_FOUND);
        expect(error.response).toBeInstanceOf(ErrorDTO);
        expect(error.response).toHaveProperty('error');
        expect(error.response.error).toBe(1002);
        expect(error.response).toHaveProperty('message');
        expect(error.response.message).toContain(positionId.toString());
      }
    });

    it('given a valid position id but no connection to backend, it should return 503 status', async () => {
      positionsService.getPositionById.mockRejectedValue(
        new PositionsBackendNotAvailableException(),
      );

      try {
        await positionController.getPositionById(99999);
      } catch (error) {
        expect(error).toBeInstanceOf(HttpException);
        expect(error.status).toEqual(HttpStatus.SERVICE_UNAVAILABLE);
        expect(error.response).toBeInstanceOf(ErrorDTO);
        expect(error.response).toHaveProperty('error');
        expect(error.response.error).toBe(1000);
        expect(error.response).toHaveProperty('message');
      }
    });
  });

  function checkPosition(position: PositionDTO | undefined) {
    if (!position) {
      fail('Position is undefined');
    }

    expect(position).toHaveProperty('id');
    expect(position).toHaveProperty('title');
    expect(position).toHaveProperty('description');
    expect(position).toHaveProperty('status');
    expect(position).toHaveProperty('tags');
    position.tags?.forEach((tag: TagDTO) => {
      expect(tag).toHaveProperty('name');
    });

    expect(position).toHaveProperty('applications');
    expect(position).toHaveProperty('creationDate');
  }

  function checkPositionDetail(
    position: PositionDetailDTO | undefined,
    id: number,
  ) {
    if (!position) {
      fail('Position is undefined');
    }

    checkPosition(position);
    expect(position).toHaveProperty('id', id);
    expect(position).toHaveProperty('requirements');

    position.requirements?.forEach((requirement) => {
      expect(requirement).toHaveProperty('key');
      expect(requirement).toHaveProperty('value');
      expect(requirement).toHaveProperty('description');
      expect(requirement).toHaveProperty('isMandatory');
    });

    expect(position).toHaveProperty('tasks');
    position.tasks?.forEach((task) => {
      expect(task).toHaveProperty('description');
    });

    expect(position).toHaveProperty('benefits');
    position.benefits?.forEach((benefit) => {
      expect(benefit).toHaveProperty('description');
    });
  }
});
