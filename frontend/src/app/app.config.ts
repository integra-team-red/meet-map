import {ApplicationConfig, importProvidersFrom, provideBrowserGlobalErrorListeners} from '@angular/core';
import {provideRouter, withComponentInputBinding} from '@angular/router';
import {provideHttpClient, withInterceptors} from '@angular/common/http';

import {routes} from './app.routes';
import {providePrimeNG} from 'primeng/config';

import {ApiModule, Configuration} from '../../typescript-client';
import {authInterceptor} from './core/interceptors/auth-interceptor';
import {storedThemeName, THEMES} from './theme/theme';

export const appConfig: ApplicationConfig = {
  providers: [
    provideHttpClient(withInterceptors([authInterceptor])),
    importProvidersFrom(
      ApiModule.forRoot(() => new Configuration({
        basePath: ''
      }))
    ),
    provideBrowserGlobalErrorListeners(),
    provideRouter(routes, withComponentInputBinding()),
    providePrimeNG({
      theme: {
        preset: THEMES[storedThemeName()].preset,
        options: {
          darkModeSelector: '.my-app-dark'
        }
      }
    })
  ]
};
