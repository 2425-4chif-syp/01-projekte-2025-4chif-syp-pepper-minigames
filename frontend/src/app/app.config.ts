import { ApplicationConfig, provideZoneChangeDetection, InjectionToken } from '@angular/core';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import { provideClientHydration } from '@angular/platform-browser';
import { provideHttpClient, withFetch } from "@angular/common/http";
export const STORY_URL= new InjectionToken<string>('STORY_URL');
export const appConfig: ApplicationConfig = {
  providers: [provideZoneChangeDetection({ eventCoalescing: true }),
  {
    provide: STORY_URL,
    useValue: 'http://vm88.htl-leonding.ac.at:8080/api/'
  },
  provideRouter(routes), provideClientHydration(), provideHttpClient(withFetch())]
};
