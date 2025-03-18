import { Component, inject, signal } from '@angular/core';
import { CdkDragDrop, DragDropModule, moveItemInArray } from '@angular/cdk/drag-drop';
import { CommonModule } from '@angular/common';
import { ImageServiceService } from '../service/image-service.service';
import { ImageModel } from '../models/image.model';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

interface Scene {
  speech: string;
  movement: string;
  duration: number;
  image: string;
}

@Component({
  selector: 'app-createstory',
  imports: [DragDropModule, CommonModule, FormsModule],
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

  scenes: Scene[] = [];
  isSidebarVisible = false;
  selectedScene: Scene | null = null;

  titleImage: string | null = null;
  titleName: string = '';

  imagesService = inject(ImageServiceService);
  images = signal<ImageModel[]>([]);

  constructor(private route: ActivatedRoute) {}

  ngOnInit(): void {
    this.loadImages();

    this.route.paramMap.subscribe((params) => {
      const storyId = params.get('id');
      if (storyId) {
        this.loadStory(Number(storyId));
      }
    });
  }

  loadImages(): void {
    this.imagesService.getImages().subscribe({
      next: (data) => {
        this.images.set(data);
        console.log(data);
      },
      error: (err) => {
        console.error('Laden fehlgeschlagen: ' + err.message);
      },
    });
  }

  loadStory(storyId: number) {
    fetch(`http://vm88.htl-leonding.ac.at:8080/api/tagalongstories/${storyId}`)
      .then((response) => response.json())
      .then((data) => {
        this.titleName = data.name;
        this.titleImage = data.icon;
        this.loadScenes(storyId);
      })
      .catch((error) => console.error('Error loading story:', error));
  }

  loadScenes(storyId: number) {
    fetch(`http://vm88.htl-leonding.ac.at:8080/api/tagalongstories/${storyId}/steps`)
      .then((response) => response.json())
      .then((data) => {
        this.scenes = data.map((scene: { text: any; move: { name: any }; durationInSeconds: any; image: any }) => ({
          speech: scene.text,
          movement: scene.move.name,
          duration: scene.durationInSeconds,
          image: scene.image ?? 'assets/images/defaultUploadPic_50.jpg', // Ensure default
        }));
      })
      .catch((error) => console.error('Error loading scenes:', error));
  }

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

  updateImage(event: Event, scene: Scene) {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files[0]) {
      const reader = new FileReader();
      reader.onload = (e: any) => {
        scene.image = e.target.result;
      };
      reader.readAsDataURL(input.files[0]);
    }
  }

  uploadTitleImage(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files[0]) {
      const reader = new FileReader();
      reader.onload = (e: any) => {
        this.titleImage = e.target.result;
      };
      reader.readAsDataURL(input.files[0]);
    }
  }
  
  setSceneImage(scene: Scene | null, image: ImageModel) {
    if (scene) {
      scene.image = 'data:image/png;base64,' + image.base64Image;
    }
  }
  

  clearImage(scene: Scene) {
    scene.image = 'assets/images/defaultUploadPic_50.jpg';
  }

  addScene() {
    this.scenes.push({
      speech: '',
      movement: this.moveNames[0],
      duration: this.duration[0],
      image: 'assets/images/defaultUploadPic_50.jpg',
    });
  }

  deleteScene(index: number) {
    this.scenes.splice(index, 1);
  }

  toggleSidebar() {
    this.isSidebarVisible = !this.isSidebarVisible;
  }

  async saveButton() {
    const storyData = {
      name: this.titleName,
      icon: this.titleImage,
      gameType: { id: 'TAG_ALONG_STORY', name: 'Mitmachgeschichten' },
      enabled: true,
    };

    await fetch(`http://vm88.htl-leonding.ac.at:8080/api/tagalongstories`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(storyData),
    })
      .then((response) => response.json())
      .then((data) => {
        console.log('Geschichte erfolgreich gespeichert:', data);
        this.saveScenes(data.id);
      })
      .catch((error) => console.error('Fehler beim Speichern der Geschichte:', error));
  }

  saveScenes(storyId: number) {
    this.scenes.forEach((scene, index) => {
      const moveIndex = this.moveNames.indexOf(scene.movement);
      const moveId = moveIndex !== -1 ? moveIndex + 1 : 1;

      const sceneData = {
        game: {
          name: this.titleName,
          icon: this.titleImage,
          gameType: { id: 'TAG_ALONG_STORY', name: 'Mitmachgeschichten' },
          enabled: true,
        },
        index: index + 1,
        image: scene.image,
        image_desc: 'Beschreibung des Bildes',
        move: {
          id: moveId,
          name: scene.movement,
          description: this.moveNames[moveIndex] || 'Unbekannt',
        },
        text: scene.speech,
        durationInSeconds: scene.duration,
      };

      fetch(`http://vm88.htl-leonding.ac.at:8080/api/tagalongstories/${storyId}/steps`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(sceneData),
      })
        .then((response) => response.json())
        .then((data) => console.log('Szene erfolgreich gespeichert:', data))
        .catch((error) => console.error('Fehler beim Speichern der Szene:', error));
    });
  }
}
