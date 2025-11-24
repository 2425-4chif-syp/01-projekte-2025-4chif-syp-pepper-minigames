import { bootstrapApplication } from '@angular/platform-browser';
import { AppComponent } from './app/app.component';
import { APP_INITIALIZER } from '@angular/core';
import { provideKeycloak, KeycloakService } from 'keycloak-angular';
import { appConfig } from './app/app.config';

bootstrapApplication(AppComponent, {
  providers: [
    ...(appConfig.providers || []),

    // choose clientId depending on runtime host (local dev vs VM)
    // - when developing locally (localhost / 127.0.0.1) use `angular-frontend-local`
    // - otherwise (deployed on the VM) use `angular-frontend`
    (() => {
      const host = window.location.hostname;
      const clientId = (host === 'localhost' || host === '127.0.0.1')
        ? 'angular-frontend-local'
        : 'angular-frontend';

      return provideKeycloak({
        config: {
          url: 'https://vm107.htl-leonding.ac.at/auth',
          realm: 'pepper',
          clientId
        },
        initOptions: {
          onLoad: 'login-required',
          checkLoginIframe: false
        }
        // optional: enableBearerInterceptor: true, bearerExcludedUrls: [...]
      });
    })()
  ]
}).catch(err => console.error(err));

