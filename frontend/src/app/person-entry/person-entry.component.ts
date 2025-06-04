import { Component } from '@angular/core';

@Component({
  selector: 'app-person-entry',
  imports: [],
  templateUrl: './person-entry.component.html',
  styleUrl: './person-entry.component.css'
})
export class PersonEntryComponent {
  person = {
    firstName: '',
    lastName: '',
    roomNumber: ''
  };

  updatePerson(field: keyof typeof this.person, event: Event) {
    const inputElement = event.target as HTMLInputElement | null;
    if (inputElement && inputElement.value !== null) {
      this.person[field] = inputElement.value;
    }
  }

  onLogin(event: Event) {
    event.preventDefault(); // Verhindert Standard-Seiten-Neuladen
    console.log("Person eingeloggt:", this.person);
    alert(`Willkommen ${this.person.firstName} ${this.person.lastName}, Raum: ${this.person.roomNumber}`);
  }
}
