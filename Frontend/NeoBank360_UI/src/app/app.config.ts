import {
  ApplicationConfig,
  provideBrowserGlobalErrorListeners,
} from '@angular/core';

import { provideRouter, withRouterConfig } from '@angular/router';

import {
  provideHttpClient,
  withFetch,
  withInterceptors,
} from '@angular/common/http';

import {
  provideClientHydration,
  withEventReplay
} from '@angular/platform-browser';

import { provideCharts, withDefaultRegisterables } from 'ng2-charts';

import { routes } from './app.routes';
import { authInterceptor } from './auth/auth.interceptor';
import { tokenExpiryInterceptor } from './interceptors/token-expiry.interceptor';

export const appConfig: ApplicationConfig = {
  providers: [

    // ✅ Chart support
    provideCharts(withDefaultRegisterables()),

    provideBrowserGlobalErrorListeners(),

    // ✅ ✅ ✅ MAIN FIX (VERY IMPORTANT)
    provideRouter(
      routes,
      withRouterConfig({
        onSameUrlNavigation: 'reload'   // ✅ THIS FIXES NAVBAR DOUBLE CLICK
      })
    ),

    provideHttpClient(
      withFetch(),
      withInterceptors([
        authInterceptor,
        tokenExpiryInterceptor,
      ])
    ),

    provideClientHydration(withEventReplay()),
  ],
};
``