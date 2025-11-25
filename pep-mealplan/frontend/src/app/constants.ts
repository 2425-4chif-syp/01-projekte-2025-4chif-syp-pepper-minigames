export const API_URL = window.location.port === '4200'
  ? 'http://localhost:3000'
  : window.location.origin;

if (window.location.port === '4200') {
  console.log('Entwicklungsmodus erkannt (ng serve auf Port 4200). Verwende Backend URL:', API_URL);
} else {
  console.log('Produktionsmodus erkannt. Verwende Backend URL:', API_URL);
}