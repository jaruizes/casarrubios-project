import { Test, TestingModule } from '@nestjs/testing';
import { PositionsController } from './positions.controller';
import { PositionsService } from './positions.service';
import { Position, Tag } from './position.dto';
import { NotFoundException } from '@nestjs/common';

describe('PositionController', () => {
  let positionController: PositionsController;

  beforeEach(async () => {
    const app: TestingModule = await Test.createTestingModule({
      controllers: [PositionsController],
      providers: [PositionsService],
    }).compile();

    positionController = app.get<PositionsController>(PositionsController);
  });

  describe('getAllPositions', () => {
    it('given some positions created, it should return all positions', () => {
      let positions = positionController.getAllPositions();

      expect(positions.length).toBe(5);
      positions.forEach((position) => {
        checkPosition(position);
      });
    });
  });

  describe('getPositionById', () => {
    it('given a valid position id, it should return a position', () => {
      const positionId = 1;
      let position = positionController.getPositionById(positionId);
      expect(position).toBeDefined();
      checkPositionDetail(position, positionId);
    });

    it('given a not valid position id, it should return NotFoundException', () => {
      expect(() => positionController.getPositionById(99999)).toThrow(NotFoundException);
    });
  });

  function checkPosition(position: Position | undefined) {
    if (!position) {
      fail('Position is undefined');
    }

    expect(position).toHaveProperty('id');
    expect(position).toHaveProperty('title');
    expect(position).toHaveProperty('description');
    expect(position).toHaveProperty('status');
    expect(position).toHaveProperty('tags');
    position.tags?.forEach((tag: Tag) => {
      expect(tag).toHaveProperty('name');
    });

    expect(position).toHaveProperty('applications');
    expect(position).toHaveProperty('creationDate');
  }

  function checkPositionDetail(position: Position | undefined, id: number) {
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
