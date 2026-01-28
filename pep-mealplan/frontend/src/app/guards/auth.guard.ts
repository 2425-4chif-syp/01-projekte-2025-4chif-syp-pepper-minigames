import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, Router } from '@angular/router';
import Keycloak from 'keycloak-js';
import { RoleService } from '../services/role.service';

@Injectable({ providedIn: 'root' })
export class AuthGuard implements CanActivate {
  private keycloak?: Keycloak;

  constructor(private router: Router, private roleService: RoleService) {}

  async canActivate(route: ActivatedRouteSnapshot): Promise<boolean> {
    // Check if token already exists
    const token = localStorage.getItem('kc_token');
    if (token) {
      // Check role-based access if roles are specified in route data
      const requiredRoles = route.data?.['roles'] as string[];
      if (requiredRoles && requiredRoles.length > 0) {
        const hasAccess = this.roleService.hasAnyRole(requiredRoles);
        if (!hasAccess) {
          console.log('Access denied - insufficient roles');
          this.router.navigate(['/']);
          return false;
        }
      }
      return true;
    }

    // Initialize Keycloak if not already done
    if (!this.keycloak) {
      const isLocalhost = window.location.hostname === 'localhost' ||
                          window.location.hostname === '127.0.0.1';
      const clientId = isLocalhost ? 'angular-frontend-local' : 'angular-frontend';

      this.keycloak = new Keycloak({
        url: 'https://vm107.htl-leonding.ac.at/auth',
        realm: 'pepper',
        clientId: clientId
      });

      try {
        const authenticated = await this.keycloak.init({
          onLoad: 'login-required',
          pkceMethod: 'S256',
          checkLoginIframe: false
        });

        if (authenticated && this.keycloak.token) {
          localStorage.setItem('kc_token', this.keycloak.token);
          localStorage.setItem('kc_refresh_token', this.keycloak.refreshToken || '');

          this.setupTokenRefresh();

          // Check role-based access after login
          const requiredRoles = route.data?.['roles'] as string[];
          if (requiredRoles && requiredRoles.length > 0) {
            const hasAccess = this.roleService.hasAnyRole(requiredRoles);
            if (!hasAccess) {
              this.router.navigate(['/']);
              return false;
            }
          }

          return true;
        }
      } catch (error) {
        console.error('Keycloak initialization failed:', error);
        return false;
      }
    }

    return false;
  }

  private setupTokenRefresh(): void {
    if (!this.keycloak) return;

    setInterval(() => {
      if (this.keycloak) {
        this.keycloak.updateToken(30)
          .then(refreshed => {
            if (refreshed && this.keycloak?.token) {
              localStorage.setItem('kc_token', this.keycloak.token);
              console.log('Token refreshed');
            }
          })
          .catch(() => {
            console.log('Token refresh failed, logging out');
            localStorage.removeItem('kc_token');
            localStorage.removeItem('kc_refresh_token');
            window.location.reload();
          });
      }
    }, 10000); // Check every 10 seconds
  }

  getToken(): string | null {
    return localStorage.getItem('kc_token');
  }

  logout(): void {
    console.log('Logout initiated...');
    localStorage.removeItem('kc_token');
    localStorage.removeItem('kc_refresh_token');
    if (this.keycloak) {
      this.keycloak.logout();
    } else {
      // If keycloak instance doesn't exist, create one for logout
      const isLocalhost = window.location.hostname === 'localhost' ||
                          window.location.hostname === '127.0.0.1';
      const kc = new Keycloak({
        url: 'https://vm107.htl-leonding.ac.at/auth',
        realm: 'pepper',
        clientId: isLocalhost ? 'angular-frontend-local' : 'angular-frontend'
      });
      kc.init({}).then(() => kc.logout());
    }
  }
}
