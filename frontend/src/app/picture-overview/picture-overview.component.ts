import { CommonModule } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { get } from 'http';
import { ImageServiceService } from '../service/image-service.service';
import { ImageModel } from '../models/image.model';
import { Router } from '@angular/router';
import { RouterModule } from '@angular/router';
import { ImageDto } from '../models/imageDto.model';
import { ImageJson } from '../models/image-json.model';

@Component({
  selector: 'app-picture-overview',
  imports: [CommonModule, RouterModule],
  templateUrl: './picture-overview.component.html',
  styleUrl: './picture-overview.component.css'
})
export class PictureOverviewComponent {

  constructor(private router: Router) {}

  imagesService = inject(ImageServiceService);
  images = signal<ImageJson[]>([]);
  standartImages = signal<ImageJson[]>([])

  tmpImages = this.images()
  activeButton = signal<string>("All")

  aktiverFilter = this.showAllImages

  showAllImages(){
    this.aktiverFilter = this.showAllImages
    this.activeButton.set('All')
    this.images.set(this.standartImages())
  }

  showImageOfStories(){
    let saveArr: ImageJson[] = []
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
    let saveArr: ImageJson[] = []
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
    this.imagesService.getImageNew().subscribe(
      {
        next: data=>{
          const encodedImages = data.items.map(image => ({
          ...image,
          href: encodeURIComponent(image.href),
          originalHref: image.href 
        }));
          this.images.set(encodedImages);
          this.standartImages.set(encodedImages);
          console.log(this.images());
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

  selectedImage = signal<ImageJson | null>(null);

  openPreview(image: ImageJson) {
    this.selectedImage.set(image);
    console.log(this.selectedImage());
  }

  closePreview() {
    this.selectedImage.set(null);
  }

  downloadImage() {
    const image = this.selectedImage();
    if (!image || !image.description) return;

    const a = document.createElement('a');
    a.href =  (image as any).originalHref || decodeURIComponent(image.href);
    a.download = image.description?.replace(/\s+/g, '_') + '.png';
    a.click();
  }

  deleteImage() {
    const image = this.selectedImage();
    if (!image || !image.description) return;

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
