import { bootstrapApplication } from '@angular/platform-browser';
import { AppComponent } from './app/app.component';
import { appConfig } from './app/app.config'; // ✅ Import app.config

bootstrapApplication(AppComponent, appConfig) // ✅ Verwende appConfig statt eigene providers
  .catch(err => console.error(err));