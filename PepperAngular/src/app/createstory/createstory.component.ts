import { Component, ElementRef, inject, ViewChild, OnDestroy, AfterViewInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { RouterOutlet, RouterModule } from '@angular/router';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { STORY_URL } from '../app.config';
import { IGameType, IStep, ITagalongStory, MoveHandler } from '../../models/tagalongstories.model';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { map, Observable } from 'rxjs';
import { SafeUrl, DomSanitizer } from '@angular/platform-browser';
import Cropper from 'cropperjs';




@Component({
  selector: 'app-createstory',
  standalone: true,
  imports: [RouterModule, CommonModule, FormsModule],
  templateUrl: './createstory.component.html',
  styleUrl: './createstory.component.css',
})
export class CreatestoryComponent{
  private baseUrl = inject(STORY_URL);
  private http = inject(HttpClient);
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

  public steps: IStep[] = [];
  private defaultGameType : IGameType = {
    id: "TAG_ALONG_STORY",
    name: "Mitmachgeschichten"
  }

  @ViewChild('image', { static: false }) imageElement!: ElementRef<HTMLImageElement>;
  @ViewChild('zoomRange', { static: false }) zoomRangeElement!: ElementRef<HTMLInputElement>;

  private cropper!: Cropper;

  ngAfterViewInit(): void {
    if (!this.imageElement) {
      console.error("Fehler: imageElement wurde nicht gefunden!");
    } else {
      console.log("imageElement gefunden:", this.imageElement);
    }
  
    this.initializeCropper();
  }

  initializeCropper(): void {

    if (this.cropper) {
      this.cropper.destroy();
    }

    this.cropper = new Cropper(this.imageElement.nativeElement, {
      aspectRatio: NaN,
      viewMode: 1,
      preview: '.preview',
      crop: (event) => {
        console.log("Cropping-Daten: ", event.detail);
      },
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


  setAspectRatio(ratio: number): void {
    if (this.cropper) {
      this.cropper.setAspectRatio(ratio);
    }
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
      this.zoomRangeElement.nativeElement.value = '1'; // Reset the range input to the default zoom level
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
  

  // Default Placeholder from TagAlongStory
  tagalongstory: ITagalongStory = {
    id: 0,
    name: '',
    icon: 'string',
    gameType: this.defaultGameType,
    enabled: true,
  };

  constructor(
    private sanitizer: DomSanitizer
  ) {
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


  addNewStep(){
    this.steps.push({
      id: 0, // Temporary ID (can be updated later)
      duration: 0,
      image: 'https://fakeimg.pl/600x400?text=Bild+Hochladen',
      index: this.steps.length + 1, // Set index dynamically
      text: '',
      move: '',
      game: 0
    })
    
  }



  //#region Save TagAlongStory and Steps to DB
  saveToDb() : void{
    if (/*this.imagePreview*/ true) {
      const moveHandler = new MoveHandler();
      
      //const base64Data = this.imagePreview.replace(/^data:image\/[a-z]+;base64,/, '');
      //this.tagalongstory.icon = base64Data;

      /*if(this.tagalongstory.name == ''){
        alert("Name für die Mitmachgeschihte eingeben!")
        return;
      }*/
      const headers = new HttpHeaders({
        'Content-Type': 'application/json'
      });
      // Uploading TagAlongStory with ICON
      //this.http.post(this.baseUrl, this.tagalongstory, { headers }).subscribe(
      //  response => {
      //    console.log('Story submitted successfully:', response);
      //  },
      //  error => {
      //    console.error('Error submitting story:', error);
      //  }
      //);

      // Implement add steps to the following TagAlongStory
      this.steps.forEach(item => {
        const move = moveHandler.getMove(item.move as string);
        if (move) {
          item.move = move;
        }
      });
      
      console.log(this.steps);
      
    } else {
        console.error("No image to save!");
        return;
    }

    
  }
  //#endregion
  

}