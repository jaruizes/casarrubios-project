import { Inject, Injectable } from '@nestjs/common';
import { CACHE_MANAGER } from '@nestjs/cache-manager';
import { Cache } from 'cache-manager';
import { Notification } from './model/Notification';
import { NotificationsGateway } from '../api/websockets/notifications-gateway';

@Injectable()
export class NotificationsManagerService {
  private keys: string[] = [];
  constructor(@Inject(CACHE_MANAGER) private cacheManager: Cache, private readonly gateway: NotificationsGateway) {}

  async addNotification(notification: Notification) {
    const cacheKey = notification.id;
    this.keys.push(cacheKey);
    await this.cacheManager.set(cacheKey, notification);
    this.gateway.sendNotification(notification);
  }

  async getNotifications(): Promise<Notification[]> {
    let notifications: Notification[] = [];
    for (const key of this.keys) {
      const notification = await this.cacheManager.get(key);
      notifications.push(<Notification> notification);
    }
    return notifications;
  }
}