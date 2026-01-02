import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { UserAPIService, Resident } from '../residents-api.service';

@Component({
    selector: 'app-manage-users',
    imports: [FormsModule, CommonModule, MatProgressSpinnerModule],
    templateUrl: './manage-users.component.html'
    // styleUrls kannst du entfernen oder leer lassen
})
export class ManageUsersComponent implements OnInit {
    users: Resident[] = [];
    searchTerm: string = '';
    searchLoading = false;
    newUser: Resident = { Firstname: '', Lastname: '', DOB: '' };
    showDobInput = false;

    constructor(private userApiService: UserAPIService) {}

    ngOnInit() {
        this.loadUsers();
    }

    loadUsers() {
        this.userApiService.getResidents().subscribe({
            next: (users) => {
                users.sort(
                    (a, b) =>
                        a.Lastname.localeCompare(b.Lastname) ||
                        a.Firstname.localeCompare(b.Firstname)
                );

                users.forEach((user) => {
                    if (user.DOB && new Date(user.DOB).getFullYear() > 2100) {
                        user.DOB = '';
                    }
                });

                this.users = users;
            },
            error: () => {
                alert('Fehler beim Laden der Benutzer.');
            }
        });
    }

    filteredUsers() {
        const term = this.searchTerm.toLowerCase();
        return this.users.filter(
            (user) =>
                user.Firstname.toLowerCase().includes(term) ||
                user.Lastname.toLowerCase().includes(term)
        );
    }

    onSearchChange() {
        this.searchLoading = true;
        setTimeout(() => (this.searchLoading = false), 200);
    }

    addUser() {
        if (this.newUser.Firstname && this.newUser.Lastname) {
            const userExists = this.users.some(
                (user) =>
                    user.Firstname.toLowerCase() === this.newUser.Firstname.toLowerCase() &&
                    user.Lastname.toLowerCase() === this.newUser.Lastname.toLowerCase()
            );

            if (userExists && !this.newUser.DOB) {
                this.showDobInput = true;
                return;
            }

            if (!this.newUser.DOB) {
                this.newUser.DOB = '2130-01-01';
            }

            this.userApiService.addResident(this.newUser).subscribe({
                next: (user) => {
                    if (user.DOB === '2130-01-01') {
                        user.DOB = '2130-01-01';
                    }

                    this.users.push(user);
                    this.newUser = { Firstname: '', Lastname: '', DOB: '' };
                    this.loadUsers();
                    this.showDobInput = false;
                },
                error: () =>
                    alert('Ein Fehler ist beim Hinzufügen des Benutzers aufgetreten.')
            });
        } else {
            alert('Bitte geben Sie einen Vornamen und Nachnamen ein.');
        }
    }

    deleteUser(user: Resident) {
        const confirmation = confirm(
            `Möchten Sie ${user.Firstname} ${user.Lastname} wirklich löschen?`
        );
        if (confirmation && user.id) {
            this.userApiService.deleteResident(user.id).subscribe({
                next: () => {
                    this.users = this.users.filter((u) => u.id !== user.id);
                    alert(
                        `Benutzer ${user.Firstname} ${user.Lastname} wurde gelöscht!`
                    );
                },
                error: () =>
                    alert('Ein Fehler ist beim Löschen des Benutzers aufgetreten.')
            });
        }
    }
}
