import { Component, ElementRef, inject, ViewChild, OnDestroy, AfterViewInit, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterOutlet, RouterModule } from '@angular/router';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { STORY_URL } from '../app.config';
import { IGameType, IStep, ITagalongStory, MoveHandler } from '../../models/tagalongstories.model';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { map, Observable } from 'rxjs';
import { SafeUrl, DomSanitizer } from '@angular/platform-browser';
import Cropper from 'cropperjs';
import { ImageServiceService } from '../service/image-service.service';
import { ImageModel } from '../models/image.model';
import { log } from 'console';
import { sign } from 'crypto';
import { ResidentServiceService } from '../service/resident-service.service';
import { Person } from '../models/person.model';
import { get } from 'http';

@Component({
  selector: 'app-imageupload',
  standalone: true,
  imports: [RouterModule, CommonModule, FormsModule],
  templateUrl: './imageupload.component.html',
  styleUrl: './imageupload.component.css'
})
export class ImageUploadComponent {
 private baseUrl = inject(STORY_URL) + 'tagalongstories';
  private http = inject(HttpClient);
  public duration = [5, 10, 15];
  public uploadedImageSize = "0 x 0";
  public cropRecommans:string[] = [];
  imagesService = inject(ImageServiceService);
  images = signal<ImageModel[]>([]);
  description = signal<string>("");
  firstName = signal<string>('');
  lastName = signal<string>('');

  personService = inject(ResidentServiceService)
  persons = signal<Person[]>([]);
  personForPost = signal<Person | null>(null);
  showSuggestions: boolean = false;

  getIdOfPerson(){
    return new Promise<void>((resolve) => {
      this.personService.getResidents().subscribe({
        next: data => {
          this.persons.set(data);

          for (const person of this.persons()) {
            if (person.firstName === this.firstName() && person.lastName === this.lastName()) {
              this.personForPost.set(person);
              console.log('Found matching person:', person);
              break;
            }
          }
          resolve();
        },
        error: err => {
          alert("Fehler beim Laden der Bewohner: " + err.message);
          resolve();
        }
      });
    });
  }

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

  private defaultGameType : IGameType = {
    id: "TAG_ALONG_STORY",
    name: "Mitmachgeschichten"
  }

  @ViewChild('image', { static: false }) imageElement!: ElementRef<HTMLImageElement>;
  @ViewChild('zoomRange', { static: false }) zoomRangeElement!: ElementRef<HTMLInputElement>;


  private cropper!: Cropper;

  ngAfterViewInit(): void {
    if (!this.imageElement?.nativeElement) {
      console.error("Fehler: imageElement wurde nicht gefunden!");
      return;
    }

    if (!this.imageElement) {
      console.error("Fehler: imageElement wurde nicht gefunden!");
    }

    this.initializeCropper();
  }

  //#region CROPPER JS

  initializeCropper(): void {

    if (this.cropper) {
      this.cropper.destroy();
    }

    this.cropper = new Cropper(this.imageElement.nativeElement, {
      aspectRatio: NaN,
      viewMode: 1,
      preview: '.preview',
      cropBoxResizable: false,
      crop: (event) => {
        console.log("Cropping-Daten: ", event.detail);
      },
      ready: ()=> {
        this.setCropBoxTo1280x800();
      }
    });

    // Add event listener for the range slider
    this.zoomRangeElement.nativeElement.addEventListener('input', () => {
      const zoomValue = parseFloat(this.zoomRangeElement.nativeElement.value);

      if (this.cropper) {
        console.log("Zoom-Level:", zoomValue);
        this.cropper.zoomTo(zoomValue);
      } else {
        console.error("Cropper ist nicht initialisiert!");
      }
    });


    // Update the range slider when Cropper.js zoom changes
    (this.cropper as any).cropper.addEventListener('zoom', (event: any) => {
      const currentZoom = event.detail.ratio; // Get the current zoom level
      this.zoomRangeElement.nativeElement.value = currentZoom.toString(); // Update the range input value
    });

  }


  crop(): void {
    if (this.cropper) {
      const croppedCanvas = this.cropper.getCroppedCanvas();
      const dataURL = croppedCanvas.toDataURL('image/png');

      // To download the image
      const link = document.createElement('a');
      link.download = this.tagalongstory.name+'-cropped.png';
      link.href = dataURL;
      link.click();
    }
  }

  reset(): void {
    if (this.cropper) {
      this.cropper.reset();
      this.zoomRangeElement.nativeElement.value = '0.1'; // Reset the range input to the default zoom level
      this.setCropBoxTo1280x800();
    }
  }

  setCropBoxTo1280x800(): void {
    if (this.cropper) {
      // Get the image data
      const imageData = this.cropper.getImageData();

      // Calculate the scale factor (ratio of displayed image to natural image size)
      const scaleFactor = imageData.width / imageData.naturalWidth;

      // Define the target dimensions (1280x800)
      const targetWidth = 1280;
      const targetHeight = 800;

      if(imageData.naturalWidth < 600){
        alert("Das Bild ist schon relativ klein, deshalb funktioniert der Slider erst weiter rechts")
      }
      // Scale the target dimensions to match the displayed image size
      const scaledWidth = targetWidth * scaleFactor;
      const scaledHeight = targetHeight * scaleFactor;

      // Center the crop box
      const cropBoxX = (imageData.width - scaledWidth) / 2;
      const cropBoxY = (imageData.height - scaledHeight) / 2;

      // Set the crop box dimensions and position
      this.cropper.setCropBoxData({
        width: scaledWidth,
        height: scaledHeight,
        left: cropBoxX,
        top: cropBoxY,
      });
    }
  }

  cropTo1280x800(): void {
    if (this.cropper) {
      // Get the cropped canvas with exact dimensions
      const croppedCanvas = this.cropper.getCroppedCanvas({
        width: 1280,
        height: 800,
        fillColor: '#fff', // Background for non-image areas
        imageSmoothingEnabled: true,
        imageSmoothingQuality: 'high',
      });

      // Convert canvas to data URL
      const dataURL = croppedCanvas.toDataURL('image/png');

      // Create a temporary download link and trigger the download
      const link = document.createElement('a');
      link.href = dataURL;
      link.download = this.tagalongstory.name+'-cropped-1280x800.png';
      document.body.appendChild(link); // Required for Firefox
      link.click();
      document.body.removeChild(link);
    }
  }

  cropVariants(): void {
    this.cropRecommans = []; // Clear the previous recommendations
    if (!this.cropper) {
      console.error("Cropper is not initialized!");
      return;
    }
    this.showSuggestions = !this.showSuggestions;
    if(!this.showSuggestions){
      this.cropRecommans = []
    }
    const imageData = this.cropper.getImageData();
    const naturalWidth = imageData.naturalWidth;
    const naturalHeight = imageData.naturalHeight;

    // Define crop regions
    const cropRegions = [
      { name: 'Top-Left', x: 0, y: 0, width: naturalWidth / 2, height: naturalHeight / 2 },
      { name: 'Bottom-Left', x: 0, y: naturalHeight / 2, width: naturalWidth / 2, height: naturalHeight / 2 },
      { name: 'Middle', x: naturalWidth / 4, y: naturalHeight / 4, width: naturalWidth / 2, height: naturalHeight / 2 },
      { name: 'Top-Right', x: naturalWidth / 2, y: 0, width: naturalWidth / 2, height: naturalHeight / 2 },
      { name: 'Bottom-Right', x: naturalWidth / 2, y: naturalHeight / 2, width: naturalWidth / 2, height: naturalHeight / 2 },
    ];

    cropRegions.forEach((region, index) => {
      // Set the crop box position and size for the current region
      this.cropper.setData({
        x: region.x,
        y: region.y,
        width: region.width,
        height: region.height,
      });

      const croppedCanvas = this.cropper.getCroppedCanvas({
        width: 1280, // Set output width
        height: 800, // Set output height
        fillColor: '#fff', // Background for non-image areas
        imageSmoothingEnabled: true,
        imageSmoothingQuality: 'high',
      });

      // Convert canvas to data URL
      const dataURL = croppedCanvas.toDataURL('image/png');

      // Log the cropped image data URL
      console.log(`Crop Region: ${region.name}`, dataURL);

      // Add the data URL to the crop recommendations array
      this.cropRecommans.push(dataURL);

      // Log the final array of crop recommendations after all regions are processed
      if (index === cropRegions.length - 1) {
        console.log(this.cropRecommans);
      }

    });
    this.setCropBoxTo1280x800();
  }

  downloadCropRecommans(imageUrl: string) {
    const img = new Image();
    img.crossOrigin = 'anonymous'; // For CORS if needed
    img.src = imageUrl;

    img.onload = () => {
      // Create canvas
      const canvas = document.createElement('canvas');
      const ctx = canvas.getContext('2d')!;

      // Set canvas dimensions
      canvas.width = 1280;
      canvas.height = 800;

      // Draw image resized to 1280x800
      ctx.drawImage(img, 0, 0, 1280, 800);

      // Convert to blob and trigger download
      canvas.toBlob(blob => {
        if (blob) {
          const url = URL.createObjectURL(blob);
          const a = document.createElement('a');
          a.href = url;
          a.download = `resized-image-${Date.now()}.jpg`;
          document.body.appendChild(a);
          a.click();
          document.body.removeChild(a);
          URL.revokeObjectURL(url);
        }
      }, 'image/jpeg', 0.9);
    };

    img.onerror = () => {
      console.error('Failed to load image');
    };
  }

  // #endregion

  // Default Placeholder from TagAlongStory
  tagalongstory: ITagalongStory = {
    id: 0,
    name: '',
    icon: 'string',
    gameType: this.defaultGameType,
    enabled: true,
  };

  // State management for story creation flow
  isFromCreateStory: boolean = false;

  constructor(
    private sanitizer: DomSanitizer,
    private router: Router
  ) {
    // Check if coming from createstory
    this.isFromCreateStory = !!sessionStorage.getItem('pendingStoryState');
  }

  //#region Handling File when clicked on Text

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files[0]) {
      const file = input.files[0];
      const reader = new FileReader();

      reader.onload = (e) => {
        if (this.imageElement) {
          if(this.cropper){
            this.cropper.destroy()
          }
          const img = new Image();
          img.src = e.target?.result as string;
          img.onload = () => {
            this.uploadedImageSize = img.naturalWidth + " x "+ img.naturalHeight;
          }
          this.imageElement.nativeElement.src = e.target?.result as string;
          this.initializeCropper();
        } else {
          console.error("Fehler: this.imageElement ist undefined!");
        }
      };

      reader.readAsDataURL(file);
    }
  }

  //#endregion

  isDragging = false;

  onDragOver(event: DragEvent) {
    event.preventDefault();
    this.isDragging = true;
  }

  onDragLeave(event: DragEvent) {
    event.preventDefault();
    this.isDragging = false;
  }

  onDrop(event: DragEvent) {
    event.preventDefault();
    this.isDragging = false;

    if (event.dataTransfer?.files) {
      const file = event.dataTransfer.files[0];
      this.handleFile(file);
    }
  }

  private handleFile(file: File) {
    if (file.type.startsWith('image/')) {
      const reader = new FileReader();

      reader.onload = (e) => {
        if (this.imageElement) {
          if(this.cropper){
            this.cropper.destroy()
          }
          const img = new Image();
          img.src = e.target?.result as string;
          img.onload = () => {
            this.uploadedImageSize = img.naturalWidth + " x "+ img.naturalHeight;
          }


          this.imageElement.nativeElement.src = e.target?.result as string;
          this.initializeCropper();
        } else {
          console.error("Fehler: this.imageElement ist undefined!");
        }
      };

      reader.readAsDataURL(file);
    } else {
      console.warn('Invalid file type');
    }
  }


  //#region Save TagAlongStory and Steps to DB
  async saveToDb() : Promise<void>{
    const croppedCanvas = this.cropper.getCroppedCanvas({
      width: 1280,
      height: 800,
      fillColor: '#fff', // Background for non-image areas
      imageSmoothingEnabled: true,
      imageSmoothingQuality: 'high',
    });

    const newImageBase64 = croppedCanvas.toDataURL('image/png').split(',')[1];
    let imageUpload: ImageModel;

    if(this.firstName() === '' && this.lastName() === ''){
      imageUpload = {
        description: this.description(),
        person: null,
        base64Image: newImageBase64
      };
    } else {
      // Wait for the person to be fetched and set
      await this.getIdOfPerson();
      console.log('Person after fetch:', this.personForPost());
      imageUpload = {
        description: this.description(),
        person: this.personForPost(),
        base64Image: newImageBase64
      };
    }

    const newImage = croppedCanvas.toDataURL('image/png');

    console.log(croppedCanvas);

    this.imagesService.uploadImage(imageUpload).subscribe(
      {
        next: data=>{
          console.log(data);
          window.location.reload();
        },
        error: err=>{
          "Upload fehlgeschlagen" + err.message;
        },
      }
    )
  }
  //#endregion

  // Neue Methode für Titel-Bild Upload aus CreateStory
  saveAndReturnTitleImage() {
    if (!this.cropper) {
      console.error('Cropper not initialized');
      return;
    }

    // Get the cropped canvas with exact dimensions
    const croppedCanvas = this.cropper.getCroppedCanvas({
      width: 1280,
      height: 800,
      fillColor: '#fff',
      imageSmoothingEnabled: true,
      imageSmoothingQuality: 'high',
    });

    // Convert to data URL
    const dataURL = croppedCanvas.toDataURL('image/png');

    // Store the cropped image in sessionStorage
    sessionStorage.setItem('croppedTitleImage', dataURL);

    // Navigate back to createstory
    this.router.navigate(['/createstory']);
  }

  // Neue Methode für Szenen-Bild Upload aus CreateStory
  saveAndReturnSceneImage() {
    if (!this.cropper) {
      console.error('Cropper not initialized');
      return;
    }

    // Get the cropped canvas with exact dimensions
    const croppedCanvas = this.cropper.getCroppedCanvas({
      width: 1280,
      height: 800,
      fillColor: '#fff',
      imageSmoothingEnabled: true,
      imageSmoothingQuality: 'high',
    });

    // Convert to data URL
    const dataURL = croppedCanvas.toDataURL('image/png');

    // Store the cropped image in sessionStorage
    sessionStorage.setItem('croppedSceneImage', dataURL);

    // Navigate back to createstory
    this.router.navigate(['/createstory']);
  }

  onCancel() {
    if (this.isFromCreateStory) {
      // If coming from createstory, clean up and return without saving
      sessionStorage.removeItem('pendingStoryState');
      this.router.navigate(['/createstory']);
    } else {
      // Original behavior for regular image upload
      window.location.href = '/tagalongstory';
    }
  }

  // Helper method to get the image type from session storage
  getImageType(): string {
    const pendingState = sessionStorage.getItem('pendingStoryState');
    if (pendingState) {
      const storyState = JSON.parse(pendingState);
      return storyState.imageType || 'title';
    }
    return 'title';
  }
}
