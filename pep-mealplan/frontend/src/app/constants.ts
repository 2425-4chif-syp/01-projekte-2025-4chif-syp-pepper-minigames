import { isDevMode } from '@angular/core';

function getProdApiBase(): string {
  if (typeof document === 'undefined') {
    return '';
  }

  const basePath = new URL(document.baseURI).pathname.replace(/\/$/, '');
  return basePath === '/' ? '' : basePath;
}

export const API_URL = isDevMode() ? 'http://localhost:8080' : getProdApiBase();
