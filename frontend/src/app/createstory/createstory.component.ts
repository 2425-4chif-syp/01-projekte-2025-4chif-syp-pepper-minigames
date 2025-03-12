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
  public duration = [5, 10, 15];
  public moves = [
    'emote_hurra',
    'essen',
    'gehen',
    'hand_heben',
    'highfive_links',
    'highfive_rechts',
    'klatschen',
    'strecken',
    'umher_sehen',
    'winken',
  ];
  public moveNames = [
    'Hurra',
    'Essen',
    'Gehen',
    'Hand heben',
    'Highfive links',
    'Highfive rechts',
    'Klatschen',
    'Strecken',
    'Umher sehen',
    'Winken',
  ];
  
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

  updateMovement(event: Event, scene: Scene) {
    const selectElement = event.target as HTMLSelectElement;
    scene.movement = selectElement.value;
  }

  updateDuration(event: Event, scene: Scene) {
    const selectElement = event.target as HTMLSelectElement;
    scene.duration = Number(selectElement.value);
  }
}