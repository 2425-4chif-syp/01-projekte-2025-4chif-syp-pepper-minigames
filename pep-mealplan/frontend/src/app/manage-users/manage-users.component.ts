import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { UserAPIService } from '../services/residents-api.service';
import { Resident } from '../models/resident.model';

import { CardModule } from 'primeng/card';
import { InputTextModule } from 'primeng/inputtext';
import { ButtonModule } from 'primeng/button';
import { TableModule } from 'primeng/table';
import { AvatarModule } from 'primeng/avatar';
import { TagModule } from 'primeng/tag';
import { TooltipModule } from 'primeng/tooltip';
import { DatePickerModule } from 'primeng/datepicker';
import { ProgressSpinnerModule } from 'primeng/progressspinner';

@Component({
    selector: 'app-manage-users',
    imports: [
        FormsModule,
        CommonModule,
        CardModule,
        InputTextModule,
        ButtonModule,
        TableModule,
        AvatarModule,
        TagModule,
        TooltipModule,
        DatePickerModule,
        ProgressSpinnerModule
    ],
    templateUrl: './manage-users.component.html',
    standalone: true
})
export class ManageUsersComponent implements OnInit {
    users: Resident[] = [];
    searchTerm: string = '';
    searchLoading = false;
    newUser: Resident = { firstname: '', lastname: '', dob: '' };
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
                        a.lastname.localeCompare(b.lastname) ||
                        a.firstname.localeCompare(b.firstname)
                );

                users.forEach((user) => {
                    if (user.dob && new Date(user.dob).getFullYear() > 2100) {
                        user.dob = '';
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
                user.firstname.toLowerCase().includes(term) ||
                user.lastname.toLowerCase().includes(term)
        );
    }

    onSearchChange() {
        this.searchLoading = true;
        setTimeout(() => (this.searchLoading = false), 200);
    }

    addUser() {
        if (this.newUser.firstname && this.newUser.lastname) {
            const userExists = this.users.some(
                (user) =>
                    user.firstname.toLowerCase() === this.newUser.firstname.toLowerCase() &&
                    user.lastname.toLowerCase() === this.newUser.lastname.toLowerCase()
            );

            if (userExists && !this.newUser.dob) {
                this.showDobInput = true;
                return;
            }

            if (!this.newUser.dob) {
                this.newUser.dob = '2130-01-01';
            }

            this.userApiService.addResident(this.newUser).subscribe({
                next: (user) => {
                    if (user.dob === '2130-01-01') {
                        user.dob = '2130-01-01';
                    }

                    this.users.push(user);
                    this.newUser = { firstname: '', lastname: '', dob: '' };
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
            `Möchten Sie ${user.firstname} ${user.lastname} wirklich löschen?`
        );
        if (confirmation && user.id) {
            this.userApiService.deleteResident(user.id).subscribe({
                next: () => {
                    this.users = this.users.filter((u) => u.id !== user.id);
                    alert(
                        `Benutzer ${user.firstname} ${user.lastname} wurde gelöscht!`
                    );
                },
                error: () =>
                    alert('Ein Fehler ist beim Löschen des Benutzers aufgetreten.')
            });
        }
    }
}
