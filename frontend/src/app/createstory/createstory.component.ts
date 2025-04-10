import { Component, inject, signal } from '@angular/core';
import { CdkDragDrop, DragDropModule, moveItemInArray } from '@angular/cdk/drag-drop';
import { CommonModule } from '@angular/common';
import { ImageServiceService } from '../service/image-service.service';
import { ImageModel } from '../models/image.model';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import e, { Router } from 'express';

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
  imageBase64: string | null = null;
  scenenBilder: string[] = [];



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

  storyId: number | null = null;

  constructor(private route: ActivatedRoute) {}

  service = inject(ImageServiceService)

  ngOnInit(): void {
    this.loadImages();

    this.route.paramMap.subscribe((params) => {
      const storyId = params.get('id');
      if (storyId) {
        this.loadStory(Number(storyId));
      }
    });
  }

  disableSaveButton(){
    return this.scenes.length === 0 || this.titleName === "" || this.titleImage == null
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
    console.log(storyId)
    this.storyId = storyId


    this.service.getImageBase64(storyId).subscribe({
      next: data => {
        console.log("Daten von der neuen Methode:")
        console.log(data)
        this.imageBase64 = data
       // this.titleImage = this.imageBase64
        if(data != null){
          this.scenenBilder.push(data)
        }
      },
      error: error => {
        console.log("fehler beim titelbild")
        alert("fehler beim laden des bildes " + error.message)}
    })

    this.service.getTitleImage(storyId).subscribe({
      next: data => {
        console.log("Titelbild erhalten:", data);
        if (data) {
          this.titleImage = 'data:image/png;base64,' + data;
        } else {
          this.titleImage = null;
        }
      },
      error: error => {
        console.log("Fehler beim Laden des Titelbildes:", error);
        alert("Fehler beim Laden des Titelbildes: " + error.message);
      }
    });


    fetch(`/api/tagalongstories/${storyId}`)
    .then(response => response.json())
    .then(data => {
      console.log(data.name);
      this.titleName = data.name
      this.loadScenes(storyId);

    })
    .catch(error => console.error('Fehler beim Abrufen:', error));

    fetch(`/api/tagalongstories/${storyId}`)
      .then((response) => response.json())
      .then((data) => {
        //this.titleName = data.name;
       // this.titleImage = data.icon;
       // this.loadScenes(storyId);
      })
      .catch((error) => console.error('Error loading story:', error));


  }

  loadScenes(storyId: number) {
    fetch(`/api/tagalongstories/${storyId}/steps`)
      .then((response) => response.json())
      .then((data) => {
        let i = 0;
        this.scenes = data.map((scene: { text: any; move: { id: number; name: string }; durationInSeconds: any; image: any }) => {
          const moveIndex = scene.move.id - 1;
          return {
            speech: scene.text,
            movement: this.moveNames[moveIndex] || scene.move.name,
            duration: +scene.durationInSeconds, // Konvertiere explizit zu einer Zahl
            image: this.scenenBilder[i++]?? 'assets/images/defaultUploadPic_50.jpg',
          };

        });
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
    if(confirm("Sind Sie sicher dass Sie das Bild entfernen möchten?")){
      scene.image = 'assets/images/defaultUploadPic_50.jpg';
    }
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
    if(confirm("Sind Sie sicher dass Sie diese Scene löschen möchten?")){
      this.scenes.splice(index, 1);
    }
  }

  toggleSidebar() {
    this.isSidebarVisible = !this.isSidebarVisible;
  }

  async saveButton() {
    if (!this.titleName || !this.titleImage) {
      console.error('Titel oder Bild fehlen');
      return;
    }

    const storyData = {
      name: this.titleName,
      icon: this.titleImage,
      gameType: { id: 'TAG_ALONG_STORY', name: 'Mitmachgeschichten' },
      enabled: true,
    };

    try {
      let response;

      if (this.storyId) {
        // **UPDATE bestehende Geschichte**
        response = await fetch(`/api/tagalongstories/${this.storyId}`, {
          method: 'PUT',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(storyData),
        });
      } else {
        // **NEUE Geschichte erstellen**
        response = await fetch(`/api/tagalongstories`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(storyData),
        });
      }

      if (!response.ok) {
        throw new Error(`Fehler beim Speichern: ${response.statusText}`);
      }

      const data = await response.json();
      console.log(`Geschichte gespeichert mit ID: ${data.id}`);

      this.storyId = data.id; // Speichert die ID für spätere Updates

      await this.saveScenes();
      window.location.href = '/tagalongstory';

    } catch (error) {
      console.error('Fehler beim Speichern der Geschichte:', error);
    }
  }


  async saveScenes() {
    if (!this.storyId) {
      console.error('Keine Story-ID vorhanden.');
      return;
    }

    try {
      // **1. Alle bestehenden Szenen löschen**
      await fetch(`/api/tagalongstories/${this.storyId}/steps`, {
        method: 'DELETE',
        headers: { 'Content-Type': 'application/json' },
      });
      console.log('Alle alten Szenen gelöscht.');

      // **2. Neue Szenen speichern**
      for (const [index, scene] of this.scenes.entries()) {
        const moveIndex = this.moveNames.indexOf(scene.movement);
        const moveId = moveIndex !== -1 ? moveIndex + 1 : 1;

        const sceneData = {
          game: {
            name: this.titleName,
            icon: this.titleImage,
            gameType: { id: 'TAG_ALONG_STORY', name: 'Mitmachgeschichten' },
            enabled: true
          },
          index: index + 1,
          image: scene.image,
          image_desc: 'Beschreibung des Bildes',
          move: { id: moveId, name: scene.movement, description: this.moves[moveIndex] || 'Unbekannt' },
          text: scene.speech,
          durationInSeconds: scene.duration,
        };

        const response = await fetch(`/api/tagalongstories/${this.storyId}/steps`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(sceneData),
        });

        if (!response.ok) {
          throw new Error(`Fehler beim Speichern der Szene: ${response.statusText}`);
        }

        const data = await response.json();
        console.log(`Szene ${index + 1} erfolgreich gespeichert mit ID: ${data.id}`);
      }
    } catch (error) {
      console.error('Fehler beim Speichern der Szenen:', error);
    }
  }



}
