import { Module } from '@nestjs/common';
import { PositionsModule } from './positions/positions.module';
import { ApplicationsModule } from './applications/applications.module';

@Module({
  imports: [PositionsModule, ApplicationsModule],
})
export class AppModule {}
