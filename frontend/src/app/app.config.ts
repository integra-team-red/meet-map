import {ApplicationConfig, importProvidersFrom, provideBrowserGlobalErrorListeners} from '@angular/core';
import {provideRouter, TitleStrategy, withComponentInputBinding} from '@angular/router';
import {provideHttpClient, withInterceptors} from '@angular/common/http';

import {routes} from './app.routes';
import {providePrimeNG} from 'primeng/config';

import {ApiModule, Configuration} from '../../typescript-client';
import {authInterceptor} from './core/interceptors/auth-interceptor';
import {storedThemeName, THEMES} from './theme/theme';
import {MessageService} from 'primeng/api';
import {PageTitleStrategy} from './core/page-title/page-title-strategy';

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
    {provide: TitleStrategy, useClass: PageTitleStrategy},
    providePrimeNG({
      theme: {
        preset: THEMES[storedThemeName()].preset,
        options: {
          darkModeSelector: '.my-app-dark'
        }
      }
    }),
    MessageService,
  ]
};
