import { Module } from '@nestjs/common';
import { PositionsModule } from './positions/positions.module';
import { ApplicationsModule } from './applications/applications.module';
import { ConfigModule } from '@nestjs/config';

@Module({
  imports: [
    ConfigModule.forRoot({ isGlobal: true }),
    PositionsModule,
    ApplicationsModule,
  ],
})
export class AppModule {}
