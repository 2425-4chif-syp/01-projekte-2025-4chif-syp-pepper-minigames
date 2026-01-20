import { Component, OnInit } from '@angular/core';
import { UserAPIService } from '../services/residents-api.service';
import { Resident } from '../models/resident.model';
import { RouterLink } from '@angular/router';

import { ButtonModule } from 'primeng/button';
import { AvatarModule } from 'primeng/avatar';

@Component({
    selector: 'app-select-user',
    templateUrl: './select-user.component.html',
    styleUrls: ['./select-user.component.scss'],
    imports: [RouterLink, ButtonModule, AvatarModule],
    standalone: true
})
export class SelectUserComponent implements OnInit {
  users: Resident[] = [];
  filteredUsers: Resident[] = [];
  letters: string[] = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'.split('');
  filteredLetters: string[] = [];
  showNames = false;
  selectedLetter: string = '';

  constructor(private userAPIService: UserAPIService) {}

  ngOnInit(): void {
    this.userAPIService.getResidents().subscribe((data) => {
      this.users = data;
      this.updateFilteredLetters();
    });
  }

  updateFilteredLetters(): void {
    const lettersWithUsers = new Set(
      this.users.map((user) => user.lastname[0].toUpperCase())
    );
    this.filteredLetters = this.letters.filter(letter => lettersWithUsers.has(letter));
  }

  filterByLetter(letter: string): void {
    this.selectedLetter = letter;
    this.filteredUsers = this.users.filter((user) =>
      user.lastname.startsWith(letter)
    );
    this.showNames = true;
  }

  backToLetters(): void {
    this.showNames = false;
  }
}
