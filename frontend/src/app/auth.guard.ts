import { Injectable, Inject } from '@angular/core';
import { CanActivate, Router, ActivatedRouteSnapshot } from '@angular/router';
import Keycloak from 'keycloak-js';
import { RoleService } from './role.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {
  private keycloak?: Keycloak;

  constructor(private router: Router, private roleService: RoleService) {}

  async canActivate(route: ActivatedRouteSnapshot): Promise<boolean> {
    // Prüfe ob Token bereits vorhanden
    const token = localStorage.getItem('kc_token');
    if (token) {
      console.log('Token found, checking roles...');
      
      // Rollen-Check für geschützte Routen
      const requiredRoles = route.data?.['roles'] as string[];
      if (requiredRoles && !this.roleService.hasAnyRole(requiredRoles)) {
        console.log('Access denied - insufficient roles. Required:', requiredRoles, 'User has:', this.roleService.getRoles());
        this.router.navigate(['/']); // Redirect zu Homepage
        return false;
      }
      
      return true;
    }

    // Keycloak initialisieren falls noch nicht geschehen
    if (!this.keycloak) {
      const isLocalhost = window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1';
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
          
          console.log('Authentication successful');
          console.log('User roles:', this.roleService.getRoles());
          
          // Nach Login auch Rollen prüfen
          const requiredRoles = route.data?.['roles'] as string[];
          if (requiredRoles && !this.roleService.hasAnyRole(requiredRoles)) {
            console.log('Access denied after login - insufficient roles');
            this.router.navigate(['/']);
            return false;
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
            localStorage.removeItem('kc_token');
            localStorage.removeItem('kc_refresh_token');
            window.location.reload();
          });
      }
    }, 10000);
  }

  getToken(): string | null {
    return localStorage.getItem('kc_token');
  }

  // ✅ Einfache Logout-Methode die zum Login zurückführt
  logout(): void {
    console.log('Logout initiated...');
    
    // Tokens löschen
    localStorage.removeItem('kc_token');
    localStorage.removeItem('kc_refresh_token');
    
    // Keycloak-Instanz zurücksetzen
    this.keycloak = undefined;
    
    // ✅ Zur Homepage navigieren - AuthGuard wird ausgelöst und erzwingt neuen Login
    this.router.navigate(['/']).then(() => {
      console.log('Navigated to home, AuthGuard will trigger login');
      window.location.reload(); // ✅ Seite neu laden um AuthGuard zu triggern
    });
  }
}