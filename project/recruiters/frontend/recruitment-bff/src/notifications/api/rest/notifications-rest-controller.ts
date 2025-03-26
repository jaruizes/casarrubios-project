import { Controller, Get, Logger } from '@nestjs/common';
import { NotificationsManagerService } from '../../domain/notifications-manager.service';

@Controller('notifications')
export class NotificationsRestController {
  private readonly logger = new Logger(NotificationsRestController.name);

  constructor(private readonly notificationsService: NotificationsManagerService) {}

  @Get()
  async getNotifications() {
    this.logger.log(`Trying to fetch notifications`);
    return await this.notificationsService.getNotifications();
  }
}