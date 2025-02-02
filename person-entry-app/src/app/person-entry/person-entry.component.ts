import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-person-entry',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './person-entry.component.html',
  styleUrls: ['./person-entry.component.css']
})
export class PersonEntryComponent {
  person = { name: '' }; // Modell für die Eingabe

  constructor(private http: HttpClient) {}

  submitPerson() {
    this.http.post('/api/person', this.person).subscribe({
      next: (response) => console.log('Person erfolgreich gespeichert:', response),
      error: (err) => console.error('Fehler beim Speichern:', err),
    });
  }
}
