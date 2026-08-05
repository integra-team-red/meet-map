import { ApplicationConfig, provideBrowserGlobalErrorListeners, importProvidersFrom } from '@angular/core';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import {providePrimeNG} from 'primeng/config';

import Aura from '@primeuix/themes/aura';
import {ApiModule, Configuration} from '../../typescript-client';
export const appConfig: ApplicationConfig = {
  providers: [
    importProvidersFrom(
      ApiModule.forRoot(() => new Configuration({
        basePath: ''
      }))
    ),
    provideBrowserGlobalErrorListeners(),
    provideRouter(routes),
    providePrimeNG({
      theme: {
        preset: Aura,
        options: {
          darkModeSelector: '.my-app-dark'
        }
      }
    })
  ]
};
