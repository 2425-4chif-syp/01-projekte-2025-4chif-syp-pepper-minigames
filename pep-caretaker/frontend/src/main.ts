import { bootstrapApplication } from '@angular/platform-browser';
import { AppComponent } from './app/app.component';
import { APP_INITIALIZER } from '@angular/core';
import { provideKeycloak, KeycloakService } from 'keycloak-angular';
import { appConfig } from './app/app.config';

bootstrapApplication(AppComponent, {
  providers: [
    ...(appConfig.providers || []),

    provideKeycloak({
      config: {
        url: 'https://vm107.htl-leonding.ac.at/auth',
        realm: 'pepper',
        clientId: 'angular-frontend'
      },
      initOptions: {
        onLoad: 'login-required',
        checkLoginIframe: false
      }
      // optional: enableBearerInterceptor: true, bearerExcludedUrls: [...]
    })
  ]
}).catch(err => console.error(err));

