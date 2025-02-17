import { ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import {HTTP_INTERCEPTORS, provideHttpClient, withInterceptorsFromDi} from "@angular/common/http";
import {MockPositionsInterceptor} from "./infrastructure/interceptors/mock-positions.interceptor";
import {MockApplicationsInterceptor} from "./infrastructure/interceptors/mock-applications.interceptor";

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideHttpClient(
      withInterceptorsFromDi()
    ),
    { provide: HTTP_INTERCEPTORS, useClass: MockPositionsInterceptor, multi: true },
    { provide: HTTP_INTERCEPTORS, useClass: MockApplicationsInterceptor, multi: true },
  ],
};
