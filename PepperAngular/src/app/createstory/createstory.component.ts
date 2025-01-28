import { Component, ElementRef, inject, ViewChild } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { RouterOutlet, RouterModule } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { STORY_URL } from '../app.config';
import { IGameType, IStep, ITagalongStory } from '../../models/tagalongstories.model';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ImageCroppedEvent, ImageCropperComponent, LoadedImage } from 'ngx-image-cropper';
import { map, Observable } from 'rxjs';
import { SafeUrl, DomSanitizer } from '@angular/platform-browser';

@Component({
  selector: 'app-createstory',
  standalone: true,
  imports: [RouterModule, CommonModule, FormsModule, ImageCropperComponent],
  templateUrl: './createstory.component.html',
  styleUrl: './createstory.component.css',
})
export class CreatestoryComponent {
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

  @ViewChild('tbody') tbody!: ElementRef;
  @ViewChild('fileInput') fileInput: any;
  imagePreview: string = '';
  selectedFile: File | null = null;
  uploadStatus: string = '';

  imageChangedEvent: Event | null = null;
  croppedImage: SafeUrl  = 'https://fakeimg.pl/600x400?text=Bild+Hochladen';

  private defaultGameType : IGameType = {
    id: "TAG_ALONG_STORY",
    name: "Mitmachgeschichten"
  }

  public tagalongstory: ITagalongStory = {
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
  triggerFileInput() {
    this.fileInput.nativeElement.click();
  }

  onFileSelected(event: Event): void {
    this.imageChangedEvent = event;
  }
  //#endregion


  //#region ImageCropperFuncs
  fileChangeEvent(event: Event): void {
      this.imageChangedEvent = event;
  }

  imageCropped(event: ImageCroppedEvent) {
    if (event.objectUrl) {
      this.croppedImage = this.sanitizer.bypassSecurityTrustUrl(event.objectUrl);
      this.convertSafeUrlToBase64(event.objectUrl!).then((base64) => {
        this.imagePreview = base64;
        console.log('Base64 Image:', this.imagePreview);
      });
    }
  }

  imageLoaded(image: LoadedImage) {
      // show cropper
  }

  cropperReady() {
      // cropper ready
  }

  loadImageFailed() {
      // show message
  }

  convertSafeUrlToBase64(safeUrl: string): Promise<string> {
    return fetch(safeUrl)
      .then(response => response.blob())
      .then(blob => new Promise((resolve, reject) => {
        const reader = new FileReader();
        reader.onloadend = () => resolve(reader.result as string);
        reader.onerror = reject;
        reader.readAsDataURL(blob);
      }));
  }
  //#endregion

  saveToDb() : void{
    if (this.imagePreview) {
      // Remove the Base64 prefix
      const base64Data = this.imagePreview.replace(/^data:image\/[a-z]+;base64,/, '');
      // Assign the cleaned Base64 data
      this.tagalongstory.icon = base64Data;
      console.log(this.tagalongstory);
    } else {
        console.error("No image to save!");
    }
  }
  

}