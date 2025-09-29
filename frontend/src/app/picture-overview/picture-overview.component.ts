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

  transformImageUrl(originalUrl: string): string {
    if (!originalUrl) return '';
    
    try {
        const transformedUrl = originalUrl.replace(
          'vm107.htl-leonding.ac.at:8080', 
          'backend:8080'
        );
        console.log(originalUrl);
        console.log(transformedUrl);
        return encodeURIComponent(transformedUrl);
    } catch (error) {
      console.error('Error transforming URL:', error);
      return originalUrl;
    }
  }
  loadImages(): void {
    this.imagesService.getImageNew().subscribe(
      {
        next: data=>{
          const encodedImages = data.items.map(image => ({
          ...image,
          href: this.transformImageUrl(image.href),
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
    if (!image || !image.href) return;

    const originalUrl = (image as any).originalHref || decodeURIComponent(image.href);

  fetch(originalUrl)
    .then(response => {
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      return response.blob();
    })
    .then(blob => {

      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.style.display = 'none';
      a.href = url;
      a.download = (image.description?.replace(/\s+/g, '_') || 'image') + '.jpg';

      document.body.appendChild(a);
      a.click();

      window.URL.revokeObjectURL(url);
      document.body.removeChild(a);
      
      console.log('Bild erfolgreich heruntergeladen:', image.description);
    })
    .catch(error => {
      console.error('Fehler beim Herunterladen des Bildes:', error);

      const a = document.createElement('a');
      a.href = originalUrl;
      a.download = (image.description?.replace(/\s+/g, '_') || 'image') + '.jpg';
      a.target = '_blank'; 
      a.click();
    });
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
