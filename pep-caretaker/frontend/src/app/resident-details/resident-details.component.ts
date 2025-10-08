import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute, Params, Router, RouterLink } from '@angular/router';
import { Person } from '../models/person.model';
import { ResidentServiceService } from '../service/resident-service.service';
import { ImageServiceService } from '../service/image-service.service';
import { ImageModel } from '../models/image.model';
import { ImageDto } from '../models/imageDto.model';
import { PersonDto } from '../models/person-dto.model';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-resident-details',
  imports: [RouterLink, CommonModule, FormsModule],
  templateUrl: './resident-details.component.html',
  styleUrl: './resident-details.component.css'
})
export class ResidentDetailsComponent implements OnInit {
  constructor(
    private activatedRoute: ActivatedRoute,
    private router: Router
  ) {}
  
  isEditing = signal<boolean>(false);
  editFirstName = signal<string>('');
  editLastName = signal<string>('')
  editDob = signal<string>('');
  editRoomNo = signal<string>('')
  editIsWorker = signal<boolean>(false);
  editPassword = signal<string>('');
  editRole = signal<string>('');
  selectedRole = signal<string>('');

  startEditing() {
    const resident = this.residentOfId();
    if (resident) {
      this.editFirstName.set(resident.firstName);
      this.editLastName.set(resident.lastName);
      this.editRole.set(resident.isWorker ? 'worker' : 'resident');
      this.editRoomNo.set(resident.roomNo);
      this.editDob.set(resident.dob);
      this.editPassword.set(''); // Passwort wird nicht vorgefüllt aus Sicherheitsgründen
      this.editIsWorker.set(resident.isWorker);
    }
    this.isEditing.set(true);
  }

  cancelEditing() {
    this.isEditing.set(false);
  }

  onRoleChange(event: any) {
    const selectedValue = event.target.value;
    this.editIsWorker.set(selectedValue === 'worker');
  }
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
    this.getResidentById(this.actId);
  }

  getResidentById(id: number) {
    this.residentService.getResidentById(id).subscribe({
      next: data => {
        this.residentOfId.set(data);
        this.loadImages();
      },
      error: error => {
        console.error("Laden der Person fehlgeschlagen." + error.name);
      }
    });
  }

  loadImages(){
    this.imageService.getImageById(this.actId).subscribe({
      next: data => {
        this.imagesOfPerson.set(data);
      },
      error: error => {
        console.error("Laden der Bilder fehlgeschlagen." + error.name);
      }
    });
  }    

  saveChanges(){
    const updatedPerson: Person = {
      id: this.actId,
      firstName: this.editFirstName(),
      lastName: this.editLastName(),
      dob: this.editDob(),
      roomNo: this.editRoomNo(),
      isWorker: this.editIsWorker(),
      password: this.editPassword()
    };

    this.residentService.updatePerson(this.actId, updatedPerson).subscribe({
      next: (data) => {
        console.log("Person erfolgreich aktualisiert:", data);
        this.residentOfId.set(data);
        this.isEditing.set(false);
        this.getResidentById(this.actId);
      },
      error: (error) => {
        console.error("Aktualisierung der Person fehlgeschlagen." + error.name);
      }
    });
  }
}
