import { Component, inject, signal } from '@angular/core';
import { CdkDragDrop, DragDropModule, moveItemInArray } from '@angular/cdk/drag-drop';
import { CommonModule } from '@angular/common';
import { ImageServiceService } from '../service/image-service.service';
import { ImageDto } from '../models/imageDto.model';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

interface Scene {
  speech: string;
  movement: string;
  duration: number;
  image: string;
  isDragOver?: boolean;
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
  // bild von pngtree => gratis
  titleImage: string = 'assets/images/imageNotFound.png';
  titleName: string = '';

  imagesService = inject(ImageServiceService);
  images = signal<ImageDto[]>([]);

  storyId: number | null = null;
    // Drag & Drop properties
  currentDraggedImage: ImageDto | null = null;
  isDragOverTitle: boolean = false;

  // Standard-Bild als base64 String
  private defaultImageBase64: string = '';

  constructor(private route: ActivatedRoute, private router: Router) {
    // Standard-Bild beim Start laden
    this.loadDefaultImage();
  }

  service = inject(ImageServiceService)

  ngOnInit(): void {
    this.loadImages();

    // Check for returning state from image upload
    this.checkForReturnState();

    this.route.paramMap.subscribe((params) => {
      const storyId = params.get('id');
      if (storyId) {
        this.loadStory(Number(storyId));
      }
    });
  }

  // Neue Methode zum Prüfen und Wiederherstellen des States nach Rückkehr
  private checkForReturnState() {
    const pendingState = sessionStorage.getItem('pendingStoryState');
    const returnedImage = sessionStorage.getItem('croppedTitleImage');
    const returnedSceneImage = sessionStorage.getItem('croppedSceneImage');
    
    if (pendingState && (returnedImage || returnedSceneImage)) {
      // State wiederherstellen
      const storyState = JSON.parse(pendingState);
      this.titleName = storyState.titleName;
      this.scenes = storyState.scenes;
      this.storyId = storyState.storyId;
      
      if (returnedImage && storyState.imageType === 'title') {
        // Neues Titelbild setzen
        this.titleImage = returnedImage;
        console.log('Title image updated from image upload');
      } else if (returnedSceneImage && storyState.imageType === 'scene') {
        // Neues Szenenbild setzen
        const sceneIndex = storyState.sceneIndex;
        if (sceneIndex >= 0 && sceneIndex < this.scenes.length) {
          this.scenes[sceneIndex].image = returnedSceneImage;
          console.log(`Scene ${sceneIndex} image updated from image upload`);
        }
      }
      
      // Cleanup
      sessionStorage.removeItem('pendingStoryState');
      sessionStorage.removeItem('croppedTitleImage');
      sessionStorage.removeItem('croppedSceneImage');
      
      console.log('Story state restored with new image');
    }
  }  disableSaveButton(){
    return this.scenes.length === 0 || this.titleName === "" || this.titleImage === "assets/images/imageNotFound.png";
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
      next: data => {        console.log("Titelbild erhalten:", data);
        if (data) {
          this.titleImage = 'data:image/png;base64,' + data;        } else {
          this.titleImage = 'assets/images/imageNotFound.png';
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
        let i = 0;        this.scenes = data.map((scene: { text: any; move: { id: number; name: string }; durationInSeconds: any; image: any }) => {
          const moveIndex = scene.move.id - 1;
          return {
            speech: scene.text,
            movement: this.moveNames[moveIndex] || scene.move.name,
            duration: +scene.durationInSeconds, // Konvertiere explizit zu einer Zahl
            image: this.scenenBilder[i++]?? 'assets/images/imageNotFound.png',
            isDragOver: false,
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

  // Neue Methode für Navigation zur Image Upload Seite für Titelbild
  navigateToImageUpload() {
    // Story-Daten im SessionStorage zwischenspeichern
    const storyState = {
      titleName: this.titleName,
      titleImage: this.titleImage,
      scenes: this.scenes,
      storyId: this.storyId,
      returnTo: 'createstory',
      imageType: 'title'
    };
    
    sessionStorage.setItem('pendingStoryState', JSON.stringify(storyState));
    
    // Zur Image Upload Seite navigieren
    this.router.navigate(['/imageUpload']);
  }

  // Neue Methode für Navigation zur Image Upload Seite für Szenenbild
  navigateToSceneImageUpload(sceneIndex: number) {
    // Story-Daten im SessionStorage zwischenspeichern
    const storyState = {
      titleName: this.titleName,
      titleImage: this.titleImage,
      scenes: this.scenes,
      storyId: this.storyId,
      returnTo: 'createstory',
      imageType: 'scene',
      sceneIndex: sceneIndex
    };
    
    sessionStorage.setItem('pendingStoryState', JSON.stringify(storyState));
    
    // Zur Image Upload Seite navigieren
    this.router.navigate(['/imageUpload']);
  }
  setSceneImage(scene: Scene | null, image: ImageDto) {
    if (scene) {
      scene.image = 'data:image/png;base64,' + image.base64Image;
    }
  }
  clearImage(scene: Scene) {
    if(confirm("Sind Sie sicher dass Sie das Bild entfernen möchten?")){
      scene.image = 'assets/images/imageNotFound.png';
    }
  }

  // Hilfsmethode um zu prüfen, ob eine Szene das Standard-Bild verwendet
  isDefaultImage(scene: Scene): boolean {
    return scene.image === 'assets/images/imageNotFound.png' || 
           scene.image === this.defaultImageBase64;
  }

  addScene() {
    this.scenes.push({
      speech: '',
      movement: this.moveNames[0],
      duration: this.duration[0],
      image: 'assets/images/imageNotFound.png',
      isDragOver: false,
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

    // Konvertiere das Titelbild zu base64, falls es das Standard-Bild ist
    const convertedTitleImage = await this.convertImageToBase64(this.titleImage);

    const storyData = {
      name: this.titleName,
      icon: convertedTitleImage,
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
      console.log('Alle alten Szenen gelöscht.');      // **2. Neue Szenen speichern**
      for (const [index, scene] of this.scenes.entries()) {
        const moveIndex = this.moveNames.indexOf(scene.movement);
        const moveId = moveIndex !== -1 ? moveIndex + 1 : 1;

        // Konvertiere das Bild zu base64, falls es das Standard-Bild ist
        const convertedImage = await this.convertImageToBase64(scene.image);        // Konvertiere das Titelbild auch zu base64, falls es das Standard-Bild ist
        const convertedTitleImage = await this.convertImageToBase64(this.titleImage);

        const sceneData = {
          game: {
            name: this.titleName,
            icon: convertedTitleImage,
            gameType: { id: 'TAG_ALONG_STORY', name: 'Mitmachgeschichten' },
            enabled: true
          },
          index: index + 1,
          image: convertedImage,
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

  // Drag & Drop Methoden
  onDragStart(event: DragEvent, image: ImageDto) {
    this.currentDraggedImage = image;
    if (event.dataTransfer) {
      event.dataTransfer.effectAllowed = 'copy';
      event.dataTransfer.setData('text/plain', 'image');
    }
  }
  onDragOver(event: DragEvent) {
    event.preventDefault();
    if (event.dataTransfer) {
      event.dataTransfer.dropEffect = 'copy';
    }
  }

  onDropToScene(event: DragEvent, scene: Scene) {
    event.preventDefault();
    scene.isDragOver = false;
    if (this.currentDraggedImage) {
      scene.image = 'data:image/png;base64,' + this.currentDraggedImage.base64Image;
      this.currentDraggedImage = null;
    }
  }

  onDragEnterScene(event: DragEvent, scene: Scene) {
    event.preventDefault();
    scene.isDragOver = true;
  }

  onDragLeaveScene(event: DragEvent, scene: Scene) {
    event.preventDefault();
    scene.isDragOver = false;
  }

  onDropToTitle(event: DragEvent) {
    event.preventDefault();
    if (this.currentDraggedImage) {
      this.titleImage = 'data:image/png;base64,' + this.currentDraggedImage.base64Image;
      this.currentDraggedImage = null;
    }
  }
  // Methode zum Laden des Standard-Bildes als base64
  private loadDefaultImage() {
    this.loadDefaultImageAsBase64()
      .then(base64 => {
        this.defaultImageBase64 = base64;
        // Nur setzen, wenn es das Standard-Bild ist (falls bereits ein anderes Titelbild geladen wurde)
        if (this.titleImage === 'assets/images/imageNotFound.png') {
          this.titleImage = base64;
        }
      })
      .catch(err => {
        console.error('Fehler beim Laden des Standard-Bildes:', err);
      });
  }

  // Methode zum Laden des Standard-Bildes als base64
  private loadDefaultImageAsBase64(): Promise<string> {
    return new Promise((resolve, reject) => {
      fetch('assets/images/imageNotFound.png')
        .then(response => response.blob())
        .then(blob => {
          const reader = new FileReader();
          reader.onload = () => {
            if (typeof reader.result === 'string') {
              resolve(reader.result);
            } else {
              reject('Failed to convert to base64');
            }
          };
          reader.onerror = () => reject('Error reading file');
          reader.readAsDataURL(blob);
        })
        .catch(error => reject(error));
    });
  }

  // Methode zum Konvertieren von Asset-Pfad zu base64
  private async convertImageToBase64(imagePath: string): Promise<string> {
    if (imagePath.startsWith('data:')) {
      return imagePath; // Bereits base64
    }
    
    if (imagePath === 'assets/images/imageNotFound.png') {
      return await this.loadDefaultImageAsBase64();
    }
    
    return imagePath; // Fallback
  }
}
