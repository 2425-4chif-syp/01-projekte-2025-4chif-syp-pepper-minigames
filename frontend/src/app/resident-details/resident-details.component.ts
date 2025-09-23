import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute, Params, Router, RouterLink } from '@angular/router';
import { Person } from '../models/person.model';
import { ResidentServiceService } from '../service/resident-service.service';
import { ImageServiceService } from '../service/image-service.service';
import { ImageModel } from '../models/image.model';
import { ImageDto } from '../models/imageDto.model';

@Component({
  selector: 'app-resident-details',
  imports: [RouterLink],
  templateUrl: './resident-details.component.html',
  styleUrl: './resident-details.component.css'
})
export class ResidentDetailsComponent implements OnInit {
  constructor(
    private activatedRoute: ActivatedRoute,
    private router: Router
  ) {}
  
  residents = signal<Person[]>([]);
  residentService = inject(ResidentServiceService);
  actId: number = 0;
  residentOfId = signal<Person | undefined>(undefined);

  imageService = inject(ImageServiceService)
  images = signal<ImageDto[]>([]);

  imagesOfPerson = signal<ImageDto[]>([]);

  ngOnInit() {  
    this.activatedRoute.params.subscribe(
      (params: Params) => {
      this.actId=Number(params['id']);
      }
    );
    this.getAllResidents();
  }

  getAllResidents(){
    this.residentService.getResidents().subscribe({
      next: data => {
    
        this.residents.set(data);
        this.getResidentById(this.actId);
        console.log(this.residents());
        this.loadImages();
        console.log(this.imagesOfPerson());
      },
      error: error=> {
        console.error("Laden der Personen fehlgeschlagen." + error.name);
      }
    });
  }

  getResidentById(id: number) {
    this.residentOfId.set(
      this.residents().find(resident => resident.id === id)
    );
  }

  loadImages(){
    this.imageService.getImageById(this.actId).subscribe({
      next: data => {
        //this.images.set(data);
        //console.log(this.images());
        this.imagesOfPerson.set(data);
      },
      error: error => {
        console.error("Laden der Bilder fehlgeschlagen." + error.name);
      }
    });
  }    
}
