import { Routes } from '@angular/router';
import { provideRouter } from '@angular/router';
import { PersonEntryComponent } from './person-entry/person-entry.component'; // Importiere die Komponente

export const routes: Routes = [
  { path: '', redirectTo: 'person-entry', pathMatch: 'full' }, // Umleitung zur PersonEntry-Seite
  { path: 'person-entry', component: PersonEntryComponent } // Route für PersonEntry
];
