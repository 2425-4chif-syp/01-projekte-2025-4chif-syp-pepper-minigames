import { Component, OnInit } from '@angular/core';
import { UserAPIService, Resident } from '../residents-api.service';

@Component({
  selector: 'app-select-user',
  templateUrl: './select-user.component.html',
  styleUrls: ['./select-user.component.scss'],
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
    const lettersWithUsers = new Set(this.users.map(user => user.Lastname[0].toUpperCase()));
    this.filteredLetters = this.letters.filter(letter => lettersWithUsers.has(letter));
  }

  filterByLetter(letter: string): void {
    this.selectedLetter = letter;
    this.filteredUsers = this.users.filter(user => user.Lastname.startsWith(letter));
    this.showNames = true;
  }

  backToLetters(): void {
    this.showNames = false;
  }
}