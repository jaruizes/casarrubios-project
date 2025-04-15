import { Controller, Inject, Logger } from '@nestjs/common';
import { EventPattern, Payload } from '@nestjs/microservices';
import { NotificationsManagerService } from '../../domain/notifications-manager.service';
import { Notification } from '../../domain/model/Notification';

@Controller()
export class NotificationsAsyncController {
  private readonly logger = new Logger(NotificationsAsyncController.name);
  constructor(private readonly notificationsManager: NotificationsManagerService) {}

  @EventPattern(process.env.KAFKA_NOTIFICATION_TOPIC ?? 'recruitment.notifications')
  async handleNotification(@Payload() message: any) {
    this.logger.log(`[Kafka] Notification received: ${JSON.stringify(message.value)}`);
    const notification: Notification = {
      id: message.id,
      applicationId: message.applicationId,
      positionId: message.positionId,
      type: message.type,
      data: message.data
    }

    this.notificationsManager.addNotification(notification);

    console.log('[Kafka] Nueva notificación recibida:', notification);

  }
}