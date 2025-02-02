import { Component } from '@angular/core';

@Component({
  selector: 'app-home',
  standalone: true,
  template: `
    <h1>Willkommen zur Startseite</h1>
    <p>Wählen Sie eine Aktion:</p>
    <button routerLink="/person-entry" class="btn">Zur Personen-Eingabe</button>
  `
})
export class HomeComponent {}
