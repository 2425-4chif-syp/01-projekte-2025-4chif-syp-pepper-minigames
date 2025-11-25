import { Injectable } from '@angular/core';
import Keycloak, { KeycloakInstance } from 'keycloak-js';

@Injectable({ providedIn: 'root' })
export class KeycloakInitService {
  private kc: Keycloak;

  constructor() {
    this.kc = new Keycloak({
      url: 'https://vm107.htl-leonding.ac.at/auth',
      realm: 'pepper',
      clientId: 'angular-frontend'
    });
  }

  init(): Promise<boolean> {
    return this.kc.init({
      onLoad: 'login-required', 
      pkceMethod: 'S256',
    }).then(authenticated => {
      if (authenticated) {
        localStorage.setItem('kc_token', this.kc.token ?? '');
      }
      return authenticated;
    });
  }

  getToken(): string | undefined {
    return this.kc.token;
  }

  logout(): void {
    this.kc.logout();
  }
}