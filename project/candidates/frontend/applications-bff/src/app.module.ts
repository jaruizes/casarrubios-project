import { Module } from '@nestjs/common';
import { PositionsModule } from './positions/positions.module';

@Module({
  imports: [PositionsModule],
})
export class AppModule {}
