import { Component } from '@angular/core';

@Component({
  selector: 'app-createstory',
  templateUrl: './createstory.component.html',
  styleUrls: ['./createstory.component.css']
})
export class CreatestoryComponent {
  storyTitle: string = '';
  storyDescription: string = '';

  createStory() {
    // Logik zum Erstellen der Geschichte
    console.log('Geschichte erstellt:', this.storyTitle, this.storyDescription);
  }
}
