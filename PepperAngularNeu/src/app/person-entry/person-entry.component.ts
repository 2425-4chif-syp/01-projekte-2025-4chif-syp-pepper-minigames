import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-person-entry',
  templateUrl: './person-entry.component.html',
  styleUrls: ['./person-entry.component.css'],
  standalone: false
})
export class PersonEntryComponent {
  private apiUrl = 'http://localhost:8080/api/person';
  // Hier den tatsächlichen Host eintragen

  person = {
    firstName: '',
    lastName: '',
    dob: '',
    roomNo: ''
  };

  constructor(private http: HttpClient) {}

  updatePerson(field: keyof typeof this.person, event: Event) {
    const inputElement = event.target as HTMLInputElement | null;
    if (inputElement && inputElement.value !== null) {
      this.person[field] = inputElement.value;
    }
  }

  onLogin(event: Event) {
    event.preventDefault(); // Verhindert Standard-Seiten-Neuladen

    const requestBody = {
      firstName: this.person.firstName,
      lastName: this.person.lastName,
      dob: this.person.dob,  // `birthDate` wird als `dob` gesendet
      roomNo: this.person.roomNo
    };

    this.http.post(this.apiUrl, requestBody).subscribe({
      next: (response) => {
        console.log("Person erfolgreich eingeloggt:", response);
        alert(`Willkommen ${this.person.firstName} ${this.person.lastName}, Raum: ${this.person.roomNo}, Geburtstag: ${this.person.dob}`);
      },
      error: (error) => {
        console.error("Fehler beim Einloggen:", error);
        alert("Fehler beim Speichern der Personendaten.");
      }
    });
  }
}
