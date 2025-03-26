import { Injectable } from '@angular/core';
import { io, Socket } from 'socket.io-client';
import { Observable, Subject } from 'rxjs';
import {environment} from "../../../../environments/environment";

@Injectable({
  providedIn: 'root',
})
export class NotificationsService {
  private socket: Socket;
  private notificationSubject = new Subject<any>();
  private readonly notificationsUrl = environment.api.notifications;

  constructor() {
    this.socket = io(this.notificationsUrl);

    this.socket.on('connect', () => {
      console.log('WebSocket conectado');
    });

    this.socket.on('notification', (data: any) => {
      console.log('Notificación recibida:', data);
      this.notificationSubject.next(data);
    });

    this.socket.on('disconnect', () => {
      console.warn('WebSocket desconectado');
    });
  }

  get notifications$(): Observable<any> {
    return this.notificationSubject.asObservable();
  }
}
