import { CommonModule } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { CommonEngine } from '@angular/ssr/node';
import { get } from 'http';
import { ImageServiceService } from '../service/image-service.service';
import { ImageModel } from '../models/image.model';
import { Router } from '@angular/router';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-picture-overview',
  imports: [CommonModule, RouterModule],
  templateUrl: './picture-overview.component.html',
  styleUrl: './picture-overview.component.css'
})
export class PictureOverviewComponent {
  
  constructor(private router: Router) {}

  imagesService = inject(ImageServiceService);
  images = signal<ImageModel[]>([]);

  ngOnInit(): void {
    this.loadImages();
  }

  loadImages(): void {
    this.imagesService.getImages().subscribe(
      {
        next: data=>{
          this.images.set(data);
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
