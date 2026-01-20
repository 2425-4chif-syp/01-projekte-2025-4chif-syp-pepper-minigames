import { Injectable } from '@angular/core';

export interface PasswordValidation {
  isValid: boolean;
  strength: 'weak' | 'medium' | 'strong';
  score: number;
  errors: string[];
  checks: {
    minLength: boolean;
    hasUpperCase: boolean;
    hasLowerCase: boolean;
    hasNumber: boolean;
    hasSpecialChar: boolean;
  };
}

@Injectable({
  providedIn: 'root'
})
export class PasswordValidatorService {

  validatePassword(password: string): PasswordValidation {
    const checks = {
      minLength: password.length >= 8,
      hasUpperCase: /[A-Z]/.test(password),
      hasLowerCase: /[a-z]/.test(password),
      hasNumber: /[0-9]/.test(password),
      hasSpecialChar: /[!@#$%^&*(),.?":{}|<>]/.test(password)
    };

    const errors: string[] = [];
    if (!checks.minLength) errors.push('Mindestens 8 Zeichen');
    if (!checks.hasUpperCase) errors.push('Mindestens 1 Großbuchstabe');
    if (!checks.hasLowerCase) errors.push('Mindestens 1 Kleinbuchstabe');
    if (!checks.hasNumber) errors.push('Mindestens 1 Zahl');
    if (!checks.hasSpecialChar) errors.push('Mindestens 1 Sonderzeichen (!@#$%...)');

    // Score berechnen (0-5)
    const score = Object.values(checks).filter(Boolean).length;

    // Stärke bestimmen
    let strength: 'weak' | 'medium' | 'strong' = 'weak';
    if (score >= 5) strength = 'strong';
    else if (score >= 3) strength = 'medium';

    // Gültig wenn alle Checks erfüllt
    const isValid = score === 5;

    return { isValid, strength, score, errors, checks };
  }

  getStrengthColor(strength: 'weak' | 'medium' | 'strong'): string {
    switch (strength) {
      case 'strong': return 'bg-green-500';
      case 'medium': return 'bg-yellow-500';
      case 'weak': return 'bg-red-500';
    }
  }

  getStrengthText(strength: 'weak' | 'medium' | 'strong'): string {
    switch (strength) {
      case 'strong': return 'Stark';
      case 'medium': return 'Mittel';
      case 'weak': return 'Schwach';
    }
  }
}
