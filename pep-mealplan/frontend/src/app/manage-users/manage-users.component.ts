import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { UserAPIService, Resident } from '../residents-api.service';

@Component({
    selector: 'app-manage-users',
    imports: [FormsModule, CommonModule, MatProgressSpinnerModule],
    templateUrl: './manage-users.component.html',
    styleUrls: ['./manage-users.component.scss']
})
export class ManageUsersComponent implements OnInit {
  users: Resident[] = [];
  searchTerm: string = '';
  searchLoading = false;
  newUser: Resident = { Firstname: '', Lastname: '', DOB: '' };
  showDobInput = false;

  constructor(private userApiService: UserAPIService) {}

  ngOnInit() {
    this.loadUsers(); // load users at init
  }

  loadUsers() {
    this.userApiService.getResidents().subscribe({
      next: (users) => {
        // Sort users by Lastname and then Firstname
        users.sort((a, b) => a.Lastname.localeCompare(b.Lastname) || a.Firstname.localeCompare(b.Firstname));
        this.users = users;
        users.forEach(user => {
          if(user.DOB !== null && user.DOB !== '') {
            // If the Year in the Date is over 2100, the Date is a placeholder and should be removed
            if(user.DOB && new Date(user.DOB).getFullYear() > 2100) {
              user.DOB = '';
            }
          }
        });
        console.log('Users loaded:', users); //DEBUG
      },
      error: () => {
        alert('Fehler beim Laden der Benutzer.');
      }
    });
  }

  filteredUsers() {
    return this.users.filter(user =>
      user.Firstname.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
      user.Lastname.toLowerCase().includes(this.searchTerm.toLowerCase())
    );
  }

  onSearchChange() {
    this.searchLoading = true;
    setTimeout(() => (this.searchLoading = false), 200);
  }

  addUser() {
    if (this.newUser.Firstname && this.newUser.Lastname) {
      const userExists = this.users.some(user =>
        user.Firstname.toLowerCase() === this.newUser.Firstname.toLowerCase() &&
        user.Lastname.toLowerCase() === this.newUser.Lastname.toLowerCase()
      );

      if (userExists && !this.newUser.DOB) {
        this.showDobInput = true;
        return;
      }

      // Set DOB to "2130-01-01" if it is empty
      if (!this.newUser.DOB) {
        this.newUser.DOB = "2130-01-01";
      }

      this.userApiService.addResident(this.newUser).subscribe({
        next: (user) => {
          // Ensure DOB isn't reset to an empty string if it's a placeholder date
          if (user.DOB === "2130-01-01") {
            user.DOB = "2130-01-01";
          }

          this.users.push(user);
          this.newUser = { Firstname: '', Lastname: '', DOB: '' };
          this.loadUsers();
          this.showDobInput = false;
        },
        error: () => alert('Ein Fehler ist beim Hinzufügen des Benutzers aufgetreten.')
      });
    } else {
      alert('Bitte geben Sie einen Vornamen und Nachnamen ein.');
    }
  }

  deleteUser(user: Resident) {
    const confirmation = confirm(`Möchten Sie ${user.Firstname} ${user.Lastname} wirklich löschen?`);
    if (confirmation && user.id) {
      this.userApiService.deleteResident(user.id).subscribe({
        next: () => {
          this.users = this.users.filter(u => u.id !== user.id);
          alert(`Benutzer ${user.Firstname} ${user.Lastname} wurde gelöscht!`);
        },
        error: () => alert('Ein Fehler ist beim Löschen des Benutzers aufgetreten.')
      });
    }
  }
}
