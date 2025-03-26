import { Module } from '@nestjs/common';
import { HttpModule } from '@nestjs/axios';
import { SharedModule } from '../shared/shared.module';
import { NotificationsAsyncController } from './api/async/notifications-async-controller';
import { NotificationsRestController } from './api/rest/notifications-rest-controller';
import { NotificationsGateway } from './api/websockets/notifications-gateway';
import { NotificationsManagerService } from './domain/notifications-manager.service';
import { CacheModule } from '@nestjs/cache-manager';

@Module({
  controllers: [NotificationsAsyncController, NotificationsRestController],
  providers: [NotificationsGateway, NotificationsManagerService],
  exports: [NotificationsGateway],
  imports: [CacheModule.register(), HttpModule, SharedModule]
})
export class NotificationsModule {}
