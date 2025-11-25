import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class RoleService {

  getToken(): string | null {
    return localStorage.getItem('kc_token');
  }

  getRoles(): string[] {
    const token = this.getToken();
    if (!token) return [];

    try {
      // JWT Token dekodieren (Base64)
      const payload = token.split('.')[1];
      const decodedPayload = atob(payload);
      const tokenData = JSON.parse(decodedPayload);
      
      // Keycloak Rollen extrahieren
      const realmAccess = tokenData.realm_access?.roles || [];
      const resourceAccess = tokenData.resource_access?.['angular-frontend']?.roles || [];
      
      return [...realmAccess, ...resourceAccess];
    } catch (error) {
      console.error('Error decoding token:', error);
      return [];
    }
  }

  hasRole(role: string): boolean {
    return this.getRoles().includes(role);
  }

  hasAnyRole(roles: string[]): boolean {
    const userRoles = this.getRoles();
    return roles.some(role => userRoles.includes(role));
  }

  isAdmin(): boolean {
    return this.hasRole('admin');
  }

  isCaretaker(): boolean {
    return this.hasRole('caretaker');
  }

  canManageResidents(): boolean {
    return this.isAdmin(); // Nur Admin kann Bewohner verwalten
  }

  canManageStories(): boolean {
    return this.isAdmin() || this.isCaretaker(); // Beide können Geschichten verwalten
  }

  getUserInfo(): any {
    const token = this.getToken();
    if (!token) return null;

    try {
      const payload = token.split('.')[1];
      const decodedPayload = atob(payload);
      return JSON.parse(decodedPayload);
    } catch (error) {
      console.error('Error decoding user info:', error);
      return null;
    }
  }
}