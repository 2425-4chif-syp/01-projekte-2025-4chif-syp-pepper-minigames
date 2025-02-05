import { Component } from '@angular/core';

@Component({
  selector: 'login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  person = {
    username : '',
    password: ''
  };

  updatePerson(field: keyof typeof this.person, event: Event) {
    const inputElement = event.target as HTMLInputElement | null;
    if (inputElement && inputElement.value !== null) {
      this.person[field] = inputElement.value;
    }
  }

  onLogin(event: Event) {
    event.preventDefault(); 
    console.log("Person eingeloggt:", this.person);
    alert(`Willkommen ${this.person.username} ${this.person.password}`);
  }
}
