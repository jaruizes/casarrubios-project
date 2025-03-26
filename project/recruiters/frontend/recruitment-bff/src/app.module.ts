import { Module } from '@nestjs/common';
import { PositionsModule } from './positions/positions.module';
import { ApplicationsModule } from './applications/applications.module';
import { ConfigModule } from '@nestjs/config';
import { SharedModule } from './shared/shared.module';
import { GlobalPositionModule } from './global-position/global-position.module';
import { NotificationsModule } from './notifications/notifications.module';

@Module({
  imports: [
    ConfigModule.forRoot({ isGlobal: true }),
    PositionsModule,
    ApplicationsModule,
    GlobalPositionModule,
    NotificationsModule,
    SharedModule
  ],
})
export class AppModule {}
