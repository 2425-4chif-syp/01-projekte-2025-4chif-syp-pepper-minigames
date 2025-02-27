import { ApplicationConfig, InjectionToken } from '@angular/core';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { HttpClientModule, provideHttpClient } from '@angular/common/http';

import { routes } from './app.routes';

export const STORY_URL= new InjectionToken<string>('STORY_URL');

export const appConfig: ApplicationConfig = {
  providers: [provideRouter(routes, withComponentInputBinding()),
    {
      provide: STORY_URL,
      useValue: 'http://152.67.78.190:8080/tagalongstories'
    },
    provideHttpClient(),]
};
