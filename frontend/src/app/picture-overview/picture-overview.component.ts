import { Component, inject, signal } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { ImageModel } from '../models/image.model';
import { ImageService } from '../services/image.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-picture-overview',
  imports: [CommonModule, RouterModule],
  templateUrl: './picture-overview.component.html',
  styleUrl: './picture-overview.component.css'
})
export class PictureOverviewComponent {

  constructor(private router: Router) {}

  imagesService = inject(ImageService);
  images = signal<ImageModel[]>([]);
  standartImages = signal<ImageModel[]>([])

  tmpImages = this.images()
  activeButton = signal<string>("All")

  aktiverFilter = this.showAllImages

  showAllImages(){
    this.aktiverFilter = this.showAllImages
    this.activeButton.set('All')
    this.images.set(this.standartImages())
  }

  showImageOfStories(){
    let saveArr: ImageModel[] = []
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
    let saveArr: ImageModel[] = []
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

}
