  import { ApplicationConfig } from '@angular/core';
  import { provideRouter, Routes } from '@angular/router';
  import { PersonEntryComponent } from './person-entry/person-entry.component';
  import { HomeComponent } from './home.component'; // Importiere die Startseite

  const routes: Routes = [
    { path: '', component: HomeComponent }, // Startseite als Standardroute
    { path: 'person-entry', component: PersonEntryComponent } // Personen-Eingabe
  ];

  export const appConfig: ApplicationConfig = {
    providers: [
      provideRouter(routes)
    ]
  };
