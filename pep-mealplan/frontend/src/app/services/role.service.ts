import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class RoleService {

  getToken(): string | null {
    return localStorage.getItem('kc_token');
  }

  getRoles(): string[] {
    const token = this.getToken();
    if (!token) return [];

    try {
      const payload = token.split('.')[1];
      const decodedPayload = atob(payload);
      const tokenData = JSON.parse(decodedPayload);

      const realmRoles = tokenData.realm_access?.roles || [];
      const resourceRoles = tokenData.resource_access?.['angular-frontend']?.roles || [];

      return [...realmRoles, ...resourceRoles];
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

  isResident(): boolean {
    return this.hasRole('resident');
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

  getUsername(): string {
    const userInfo = this.getUserInfo();
    return userInfo?.preferred_username || userInfo?.name || 'User';
  }
}
