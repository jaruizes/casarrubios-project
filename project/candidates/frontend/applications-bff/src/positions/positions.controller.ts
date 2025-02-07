import { Controller, Get, Param, ParseIntPipe, Query } from '@nestjs/common';
import { PositionsService } from './positions.service';
import { Position } from './position.dto';

@Controller('positions')
export class PositionsController {
  constructor(private readonly positionsService: PositionsService) {}

  @Get(':id')
  getPositionById(@Param('id', ParseIntPipe) id: number): Position | undefined {
    return this.positionsService.getPositionById(id);
  }

  @Get()
  getAllPositions(
    @Query('status') status?: number,
    @Query('page') page: number = 1,
    @Query('limit') limit: number = 10,
  ): Position[] {
    return this.positionsService.getAllPositions(status, page, limit);
  }
}
