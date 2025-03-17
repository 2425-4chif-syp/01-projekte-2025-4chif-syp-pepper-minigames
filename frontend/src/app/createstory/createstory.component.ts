import { Component, inject, signal } from '@angular/core';
import { CdkDragDrop, DragDropModule, moveItemInArray } from '@angular/cdk/drag-drop';
import { CommonModule } from '@angular/common';
import { ImageServiceService } from '../service/image-service.service';
import { ImageModel } from '../models/image.model';
import { FormsModule } from '@angular/forms';

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

  ngOnInit(): void {
    this.loadImages();
  }

  loadImages(): void {
    this.imagesService.getImages().subscribe(
      {
        next: data=>{
          this.images.set(data);
          console.log(data);
        },
        error: err=>{
          console.error("Laden fehlgeschlagen: " + err.message);
        },
      }
    );
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

  clearImage(scene: Scene) {
    scene.image = 'assets/images/defaultUploadPic_5d0.jpg';
  }

  addScene() {
    this.scenes.push({
      speech: '',
      movement: this.moveNames[0],
      duration: this.duration[0],
      image: 'assets/images/defaultUploadPic_50.jpg'
    });
  }

  deleteScene(index: number) {
    this.scenes.splice(index, 1);
  }

  toggleSidebar() {
    this.isSidebarVisible = !this.isSidebarVisible;
  }

  saveButton() {
    // Zuerst die Geschichte speichern
    const storyData = {
      name: this.titleName, // Der Name der Geschichte
      icon: this.titleImage, // Das Titelbild als Base64-String
      gameType: {
        id: "TAG_ALONG_STORY", // Die ID des Spieltyps
        name: "Mitmachgeschichten" // Der Name des Spieltyps
      },
      enabled: true // Aktivierungsstatus
    };
  
    console.log('Daten werden gesendet:', storyData);
  
    // Geschichte speichern
    fetch(`http://vm88.htl-leonding.ac.at:8080/api/tagalongstories`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(storyData),
    })
      .then((response) => response.json())
      .then((data) => {
        console.log('Geschichte erfolgreich gespeichert:', data);
  
        // Nachdem die Geschichte gespeichert wurde, die Szenen speichern
        const storyId = data.id; // Annahme: Die Antwort enthält die ID der gespeicherten Geschichte
        this.saveScenes(storyId);
      })
      .catch((error) => console.error('Fehler beim Speichern der Geschichte:', error));
  }
  
  saveScenes(storyId: number) {
    // Szenen in der richtigen Reihenfolge speichern
    this.scenes.forEach((scene, index) => {
      const sceneData = {
        game: {
          name: this.titleName, // Der Name der Geschichte
          icon: this.titleImage, // Das Titelbild als Base64-String
          gameType: {
            id: "TAG_ALONG_STORY", // Die ID des Spieltyps
            name: "Mitmachgeschichten" // Der Name des Spieltyps
          },
          enabled: true // Aktivierungsstatus
        },
        index: index + 1, // Numerierung beginnt bei 1
        image: scene.image, // Das Bild der Szene
        image_desc: "Beschreibung des Bildes", // Hier kannst du eine Beschreibung hinzufügen
        move: {
          id: this.moves.indexOf(scene.movement) + 1, // ID der Bewegung
          name: scene.movement, // Name der Bewegung
          description: this.moveNames[this.moves.indexOf(scene.movement)] // Beschreibung der Bewegung
        },
        text: scene.speech, // Der Text der Szene
        durationInSeconds: scene.duration // Dauer der Szene
      };
  
      console.log('Szene wird gesendet:', sceneData);
  
      // Szene speichern
      fetch(`http://vm88.htl-leonding.ac.at:8080/api/tagalongstories/${storyId}/steps`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(sceneData),
      })
        .then((response) => response.json())
        .then((data) => console.log('Szene erfolgreich gespeichert:', data))
        .catch((error) => console.error('Fehler beim Speichern der Szene:', error));
    });
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

  selectScene(scene: Scene) {
    this.selectedScene = scene;
  }
}