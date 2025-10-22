import { Component, inject, signal } from '@angular/core';
import { CdkDragDrop, DragDropModule, moveItemInArray } from '@angular/cdk/drag-drop';
import { CommonModule } from '@angular/common';
import { ImageServiceService } from '../service/image-service.service';
import { ImageDto } from '../models/imageDto.model';
import { ImageJson } from '../models/image-json.model';
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
  // Entferne alte Base64-Properties
  // imageBase64: string | null = null;
  // scenenBilder: string[] = [];

  public duration = [5, 10, 15];
  public moves = [
    'hurra',
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
  images = signal<ImageJson[]>([]);

  storyId: number | null = null;
  
  // Variable um Scene-Daten zu speichern für späteres Upgrade
  private pendingSceneData: any[] = [];
  
  // 🚀 PERFORMANCE: Eigenschaften für übertragene Daten
  private hasExistingData: boolean = false;
  private existingStoryData: any = null;
  
    // Drag & Drop properties
  currentDraggedImage: ImageJson | null = null;
  isDragOverTitle: boolean = false;

  // Standard-Bild als base64 String
  private defaultImageBase64: string = '';

  constructor(private route: ActivatedRoute, private router: Router) {
    // Standard-Bild beim Start laden
    this.loadDefaultImage();

    // 🚀 PERFORMANCE: Check für übertragene Story-Daten
    const navigation = this.router.getCurrentNavigation();
    const existingStoryData = navigation?.extras?.state?.['existingStoryData'];
    
    if (existingStoryData) {
      console.log('🚀 PERFORMANCE BOOST: Verwende übertragene Story-Daten:', existingStoryData);
      
      // Sofort verfügbare Daten setzen
      this.titleName = existingStoryData.name;
      this.storyId = existingStoryData.id;
      
      // Titelbild wenn verfügbar sofort setzen
      if (existingStoryData.imageUrl) {
        this.titleImage = existingStoryData.imageUrl;
      }
      
      // Flag setzen, dass Daten bereits vorhanden sind
      this.hasExistingData = true;
      this.existingStoryData = existingStoryData;
    }
  }

  service = inject(ImageServiceService)

  ngOnInit(): void {
    this.route.paramMap.subscribe((params) => {
      const storyId = params.get('id');
      if (storyId) {
        this.loadStory(Number(storyId));
      } else {
        // 🚀 FIX: Auch bei neuen Geschichten Bilder für Drag & Drop laden
        console.log('🆕 Neue Geschichte: Lade Bilder für Drag & Drop');
        this.loadImages();
      }
    });

    // Check for returning state from image upload
    this.checkForReturnState();
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
  this.imagesService.getImageNew().subscribe({
    next: (data) => {
      // 🔄 NEUE REIHENFOLGE: Neueste Bilder zuerst (umgekehrte Reihenfolge)
      const reversedImages = [...data.items].reverse();
      this.images.set(reversedImages);
      console.log(`✅ Loaded ${reversedImages.length} images from image server (newest first)`);
      
      // Jetzt die wartenden Scene-Bilder upgraden
      if (this.pendingSceneData.length > 0) {
        console.log('🔄 Upgrading scene images now...');
        this.upgradeSceneImagesFromServer(this.pendingSceneData);
        this.pendingSceneData = []; // Reset nach Upgrade
      }
    },
    error: (err) => {
      console.error('Laden fehlgeschlagen: ' + err.message);
      // Fallback zur alten Methode
      this.loadImagesOld();
    },
  });
}
// Fallback-Methode (die alte Implementierung)
private loadImagesOld(): void {
  this.imagesService.getImages().subscribe({
    next: (data) => {
      // Konvertiere ImageDto[] zu ImageJson[] für Kompatibilität
      const convertedImages: ImageJson[] = data.map((imageDto: ImageDto) => ({
        id: imageDto.id,
        description: imageDto.description,
        href: '', // Dummy-Wert für href
        person: imageDto.person
      }));
      
      // 🔄 NEUE REIHENFOLGE: Auch hier umkehren
      const reversedImages = [...convertedImages].reverse();
      this.images.set(reversedImages);
      console.log(`⚠️ Fallback: Loaded ${reversedImages.length} images via old method (newest first)`);
    },
    error: (err) => {
      console.error('Laden fehlgeschlagen (old method): ' + err.message);
    },
  });
}

  loadStory(storyId: number) {
    console.log(storyId)
    this.storyId = storyId

    // 🚀 PERFORMANCE CHECK: Verwende bereits vorhandene Daten wenn verfügbar
    if (this.hasExistingData && this.existingStoryData) {
      
      // Nur Szenen laden, Titel und Bild sind bereits gesetzt
      this.loadScenes(storyId);
      
      // Titelbild-API-Call überspringen wenn bereits vorhanden
      if (this.existingStoryData.imageUrl) {
        return; // Früh beenden, da alle Daten bereits vorhanden
      }
    }

    // 🚀 OPTIMIERUNG: Szenen sofort laden (höchste Priorität)
    this.loadScenes(storyId);

    // Titel parallel laden (niedrigere Priorität) - nur wenn nicht bereits vorhanden
    if (!this.hasExistingData || !this.titleName) {
      fetch(`/api/tagalongstories/${storyId}`)
      .then(response => response.json())
      .then(data => {
        console.log(data.name);
        this.titleName = data.name
      })
      .catch(error => console.error('Fehler beim Abrufen:', error));
    } else {
      console.log('✅ Story-Titel bereits verfügbar, API-Call übersprungen');
    }

    // Titelbild parallel laden (niedrigste Priorität) - nur wenn nicht bereits vorhanden
    if (!this.hasExistingData || !this.existingStoryData?.imageUrl) {
      this.service.getTitleImage(storyId).subscribe({
        next: data => {        
          console.log("Titelbild erhalten:", data);
          if (data) {
            this.titleImage = 'data:image/png;base64,' + data;        
          } else {
            this.titleImage = 'assets/images/imageNotFound.png';
          }
        },
        error: error => {
          console.warn("Fehler beim Laden des Titelbildes:", error);
          this.titleImage = 'assets/images/imageNotFound.png';
        }
      });
    } else {
    }

    // Diese alten Base64 Aufrufe entfernen wir
    // this.service.getImageBase64(storyId).subscribe(...)
  }

  loadScenes(storyId: number) {
    fetch(`/api/tagalongstories/${storyId}/steps`)
      .then((response) => response.json())
      .then((data) => {
        // 🚀 SOFORTIGE Anzeige: Szenen ohne Bilder erstellen
        this.scenes = data.map((scene: any, index: number) => {
          const moveIndex = scene.move.id - 1;
          
          return {
            speech: scene.text,
            movement: this.moveNames[moveIndex] || scene.move.name,
            duration: +scene.durationInSeconds,
            image: 'assets/images/imageNotFound.png', // Platzhalter - KEIN Base64!
            isDragOver: false,
          };
        });
        
        console.log(`🚀 SOFORT: ${this.scenes.length} scenes visible (no images yet)`);
        
        // Scene-Daten für späteres Upgrade speichern
        this.pendingSceneData = data;
        
        // Jetzt erst die Bilder laden für die Upgrades
        console.log('📡 Loading images for scene upgrades...');
        this.loadImages();
      })
      .catch((error) => console.error('Error loading scenes:', error));
  }

  private waitForImageServerAndUpgrade(sceneData: any[]) {
    const checkImageServer = () => {
      if (this.images().length > 0) {
        console.log('📡 Image server ready, upgrading scene images');
        this.upgradeSceneImagesFromServer(sceneData);
      } else {
        setTimeout(checkImageServer, 50); // Check every 50ms
      }
    };
    checkImageServer();
  }

  private upgradeSceneImagesFromServer(sceneData: any[]) {
    sceneData.forEach((scene: any, index: number) => {
      const imageId = scene.image?.id;
      if (imageId) {
        const imageFromServer = this.images().find(img => img.id === imageId);
        if (imageFromServer) {
          this.scenes[index].image = imageFromServer.href;
          console.log(`Scene ${index}: ⚡ Upgraded to image server (ID: ${imageId})`);
        }
      }
    });
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
    const newDuration = Number(selectElement.value);
    
    console.log(`🕐 Duration Update:`, {
      oldDuration: scene.duration,
      newDuration: newDuration,
      selectValue: selectElement.value,
      sceneIndex: this.scenes.indexOf(scene)
    });
    
    scene.duration = newDuration;
    
    // Verification
    console.log(`✅ Scene duration nach Update: ${scene.duration}`);
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
  setSceneImage(scene: Scene | null, image: ImageJson) {
    if (scene) {
      scene.image = image.href;
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
    const newScene = {
      speech: '',
      movement: this.moveNames[0],
      duration: this.duration[0], // Should be 5
      image: 'assets/images/imageNotFound.png',
      isDragOver: false,
    };
    console.log(`Adding new scene with duration: ${newScene.duration}`);
    this.scenes.push(newScene);
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

    console.log('🚀 SAVE: Verarbeite Titelbild:', this.titleImage.substring(0, 50) + '...');

    // Konvertiere das Titelbild zu base64, falls es das Standard-Bild ist
    const convertedTitleImage = await this.convertImageToBase64(this.titleImage);
    
    console.log('✅ SAVE: Konvertiertes Titelbild:', convertedTitleImage.substring(0, 50) + '...');

    const storyData = {
      name: this.titleName,
      icon: convertedTitleImage,
      gameType: { id: 'TAG_ALONG_STORY', name: 'Mitmachgeschichten' },
      enabled: true,
    };

    console.log('📦 SAVE: Sende Daten an Backend:', {
      ...storyData,
      icon: storyData.icon.substring(0, 50) + '... [' + storyData.icon.length + ' chars]'
    });

    try {
      let response;

      if (this.storyId) {
        // **UPDATE bestehende Geschichte**
        console.log(`🔄 UPDATE: Aktualisiere Geschichte ID ${this.storyId}`);
        response = await fetch(`/api/tagalongstories/${this.storyId}`, {
          method: 'PUT',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(storyData),
        });
      } else {
        // **NEUE Geschichte erstellen**
        console.log('🆕 CREATE: Erstelle neue Geschichte');
        response = await fetch(`/api/tagalongstories`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(storyData),
        });
      }

      if (!response.ok) {
        const errorText = await response.text();
        console.error('❌ Backend Error:', response.status, errorText);
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

    // 🔍 DEBUG: Aktuelle Scene-Werte anzeigen
    console.log('🔍 Scenes vor dem Speichern:');
    this.scenes.forEach((scene, index) => {
      console.log(`Scene ${index + 1}: duration = ${scene.duration}, movement = ${scene.movement}`);
    });

    try {
      // **1. Alle bestehenden Szenen löschen**
      await fetch(`/api/tagalongstories/${this.storyId}/steps`, {
        method: 'DELETE',
        headers: { 'Content-Type': 'application/json' },
      });
      console.log('Alle alten Szenen gelöscht.');      // **2. Neue Szenen speichern**
      for (const [index, scene] of this.scenes.entries()) {
        console.log(`🔍 Raw scene object:`, scene);
        
        const moveIndex = this.moveNames.indexOf(scene.movement);
        const moveId = moveIndex !== -1 ? moveIndex + 1 : 1;

        // 🔍 DEBUG: Scene-Duration vor Speichern
        console.log(`Scene ${index + 1} vor Speichern:`, {
          duration: scene.duration,
          type: typeof scene.duration,
          isValid: scene.duration > 0,
          rawValue: scene.duration
        });

        // 🚀 FIX: Duration validieren und Default-Wert setzen
        const duration = (scene.duration && scene.duration > 0) ? Number(scene.duration) : 5;
        console.log(`Scene ${index + 1}: Using duration = ${duration} (original: ${scene.duration}, type: ${typeof scene.duration})`);

        // Test: Hardcode 15 to see if backend accepts it
        const testDuration = 15;
        console.log(`🧪 TEST: Sending hardcoded duration = ${testDuration}`);

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

        console.log(`📤 Sending scene ${index + 1} with durationInSeconds: ${sceneData.durationInSeconds}`);
        console.log('📤 Full payload:', JSON.stringify(sceneData, null, 2));

        const response = await fetch(`/api/tagalongstories/${this.storyId}/steps`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(sceneData),
        });

        if (!response.ok) {
          const errorText = await response.text();
          console.error('❌ Backend Error:', response.status, errorText);
          throw new Error(`Fehler beim Speichern der Szene: ${response.statusText}`);
        }

        const data = await response.json();
        console.log(`✅ Scene ${index + 1} saved with ID: ${data.id}`);
        console.log('📥 Backend response:', JSON.stringify(data, null, 2));
      }
    } catch (error) {
      console.error('Fehler beim Speichern der Szenen:', error);
    }
  }

  // Drag & Drop Methoden
  onDragStart(event: DragEvent, image: ImageJson) {
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
      scene.image = this.currentDraggedImage.href;
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
      this.titleImage = this.currentDraggedImage.href;
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
    console.log('🔄 convertImageToBase64 Input:', imagePath);
    
    if (imagePath.startsWith('data:')) {
      console.log('✅ Bereits base64, keine Konvertierung nötig');
      return imagePath; // Bereits base64
    }
    
    if (imagePath === 'assets/images/imageNotFound.png') {
      console.log('🔄 Lade Standard-Bild als base64');
      return await this.loadDefaultImageAsBase64();
    }
    
    // 🚨 FIX: Imageserver URLs zu base64 konvertieren (mit und ohne Port)
    if (imagePath.includes('vm107.htl-leonding.ac.at') && imagePath.includes('/api/image/picture/')) {
      console.log('🔄 Konvertiere Imageserver-URL zu base64:', imagePath);
      
      try {
        const response = await fetch(imagePath);
        if (!response.ok) {
          throw new Error(`HTTP ${response.status}: ${response.statusText}`);
        }
        
        const blob = await response.blob();
        console.log('📦 Blob erhalten, Größe:', blob.size, 'bytes, Typ:', blob.type);
        
        return new Promise((resolve, reject) => {
          const reader = new FileReader();
          reader.onloadend = () => {
            const result = reader.result as string;
            console.log('✅ Base64 konvertiert, Länge:', result.length, 'chars');
            console.log('🔍 Base64 Anfang:', result.substring(0, 50) + '...');
            resolve(result);
          };
          reader.onerror = () => {
            console.error('❌ FileReader Fehler');
            reject(new Error('FileReader Fehler'));
          };
          reader.readAsDataURL(blob);
        });
      } catch (error) {
        console.error('❌ Fehler beim Konvertieren der Imageserver-URL:', error);
        console.log('🔙 Fallback zu Standard-Bild');
        return await this.loadDefaultImageAsBase64(); // Fallback
      }
    }
    
    console.log('⚠️ Keine Konvertierung durchgeführt, gebe Original zurück');
    return imagePath; // Fallback
  }
}
