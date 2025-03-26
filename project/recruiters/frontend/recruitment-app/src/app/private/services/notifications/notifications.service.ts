import { Injectable } from '@angular/core';
import { io, Socket } from 'socket.io-client';
import { Observable, Subject } from 'rxjs';
import { Notification } from '../../model/notification';
import {environment} from "../../../../environments/environment";

@Injectable({
  providedIn: 'root',
})
export class NotificationsService {
  private socket: Socket;
  private notificationSubject = new Subject<any>();
  private readonly notificationsUrl = environment.api.notifications;
  private notifications: Notification[] = [];

  constructor() {
    this.socket = io(this.notificationsUrl);

    this.socket.on('connect', () => {
      console.log('WebSocket conectado');
    });

    this.socket.on('notification', (data: any) => {
      console.log('Notificación recibida:', data);
      let notification: Notification = {
        id: data.id,
        applicationId: data.applicationId,
        positionId: data.positionId,
        type: data.type,
        seen: false,
        data: data.data
      };
      this.notifications.push(notification);
      this.notificationSubject.next(notification);
    });

    this.socket.on('disconnect', () => {
      console.warn('WebSocket desconectado');
    });
  }

  get notifications$(): Observable<any> {
    return this.notificationSubject.asObservable();
  }

  getNotifications(): Notification[] {
    return this.notifications;
  }
}
