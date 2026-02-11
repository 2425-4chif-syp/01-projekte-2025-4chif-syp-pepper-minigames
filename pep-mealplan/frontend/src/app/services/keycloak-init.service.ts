import { Injectable } from '@angular/core';
import Keycloak from 'keycloak-js';

@Injectable({ providedIn: 'root' })
export class KeycloakInitService {
  private kc: Keycloak;

  constructor() {
    const isLocalhost = window.location.hostname === 'localhost' ||
                        window.location.hostname === '127.0.0.1';
    this.kc = new Keycloak({
      url: 'https://vm107.htl-leonding.ac.at/auth',
      realm: 'pepper',
      clientId: isLocalhost ? 'angular-frontend-local' : 'angular-frontend'
    });
  }

  init(): Promise<boolean> {
    return this.kc.init({
      onLoad: 'login-required',
      pkceMethod: 'S256',
      checkLoginIframe: false
    }).then(authenticated => {
      if (authenticated) {
        localStorage.setItem('kc_token', this.kc.token ?? '');
        localStorage.setItem('kc_refresh_token', this.kc.refreshToken ?? '');
      }
      return authenticated;
    });
  }

  getKeycloak(): Keycloak {
    return this.kc;
  }

  getToken(): string | undefined {
    return this.kc.token;
  }

  logout(): void {
    localStorage.removeItem('kc_token');
    localStorage.removeItem('kc_refresh_token');
    this.kc.logout();
  }
}
