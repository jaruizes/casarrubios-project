import { Module } from '@nestjs/common';
import { PositionsModule } from './positions/positions.module';
import { ApplicationsModule } from './applications/applications.module';
import { ConfigModule } from '@nestjs/config';
import { SharedModule } from './shared/shared.module';

@Module({
  imports: [
    ConfigModule.forRoot({ isGlobal: true }),
    PositionsModule,
    ApplicationsModule,
    SharedModule,
  ],
})
export class AppModule {}
