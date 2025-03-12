import { Component } from '@angular/core';
import { CdkDragDrop, DragDropModule, moveItemInArray } from '@angular/cdk/drag-drop';
import { CommonModule } from '@angular/common';


interface Scene {
  speech: string;
  movement: string;
  duration: number;
  image: string;
}

@Component({
  selector: 'app-createstory',
  imports: [DragDropModule, CommonModule],
  templateUrl: './createstory.component.html',
})
export class CreatestoryComponent {
  scenes: Scene[] = [
    {
      speech: 'Während die Zirkusvorstellung beginnt, winken wir begeistert den talentierten Artisten zu...',
      movement: 'Winken',
      duration: 15,
      image: 'assets/circus1.jpg'
    },
    {
      speech: 'Mit einem lauten "Hurra" applaudieren wir am Ende der Zirkusvorstellung...',
      movement: 'Hurra',
      duration: 5,
      image: 'assets/circus2.jpg'
    }
  ];

  drop(event: CdkDragDrop<Scene[]>) {
    moveItemInArray(this.scenes, event.previousIndex, event.currentIndex);
  }
}
