import { CommonModule } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { get } from 'http';
import { ImageServiceService } from '../service/image-service.service';
import { ImageModel } from '../models/image.model';
import { Router } from '@angular/router';
import { RouterModule } from '@angular/router';
import { ImageDto } from '../models/imageDto.model';

@Component({
  selector: 'app-picture-overview',
  imports: [CommonModule, RouterModule],
  templateUrl: './picture-overview.component.html',
  styleUrl: './picture-overview.component.css'
})
export class PictureOverviewComponent {

  constructor(private router: Router) {}

  imagesService = inject(ImageServiceService);
  images = signal<ImageDto[]>([]);
  standartImages = signal<ImageDto[]>([])

  tmpImages = this.images()
  activeButton = signal<string>("All")

  aktiverFilter = this.showAllImages

  showAllImages(){
    this.aktiverFilter = this.showAllImages
    this.activeButton.set('All')
    this.images.set(this.standartImages())
  }

  showImageOfStories(){
    let saveArr: ImageDto[] = []
    this.activeButton.set('Stories')

    this.aktiverFilter = this.showImageOfStories

    for (const element of this.standartImages()) {
      if(element.person == null){
        saveArr.push(element)
      }
    }
    console.log(saveArr)
    this.images.set(saveArr)
  }

  showImageOfPersons(){
    let saveArr: ImageDto[] = []
    this.activeButton.set('People')

    this.aktiverFilter = this.showImageOfPersons

    for (const element of this.standartImages()) {
      if(element.person != null){
        saveArr.push(element)
      }
    }
    console.log(saveArr)
    this.images.set(saveArr)
  }

  ngOnInit(): void {
    this.loadImages();
    this.aktiverFilter;
  }

  loadImages(): void {
    this.imagesService.getImages().subscribe(
      {
        next: data=>{
          this.images.set(data);
          this.standartImages.set(data)
          console.log(data);
        },
        error: err=>{
          "Laden fehlgeschlagen" + err.message;
        },
      }
    )
  };

  goToUpload() {
    this.router.navigate(['/imageUpload']);
  }

  selectedImage = signal<ImageDto | null>(null);

  openPreview(image: ImageDto) {
    this.selectedImage.set(image);
    console.log(this.selectedImage());
  }

  closePreview() {
    this.selectedImage.set(null);
  }

  downloadImage() {
    const image = this.selectedImage();
    if (!image || !image.base64Image) return;

    const a = document.createElement('a');
    a.href = 'data:image/png;base64,' + image.base64Image;
    a.download = image.description?.replace(/\s+/g, '_') + '.png';
    a.click();
  }

  deleteImage() {
    const image = this.selectedImage();
    if (!image || !image.base64Image) return;

    this.imagesService.deleteImage(image.id).subscribe({
      next: () => {
        this.closePreview()
        this.loadImages();
      },
      error: err => {
        console.error('Error deleting image:', err);
      }
    });
  }
}
