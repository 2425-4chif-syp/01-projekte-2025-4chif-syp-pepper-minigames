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
    useValue: 'http://localhost:8080/api/tagalongstories'
  }, 
  provideRouter(routes), provideClientHydration(), provideHttpClient(withFetch())]
};
