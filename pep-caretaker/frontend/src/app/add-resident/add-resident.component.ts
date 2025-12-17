import { Component, inject, signal } from '@angular/core';
import { PersonDto } from '../models/person-dto.model';
import { ResidentServiceService } from '../service/resident-service.service';
import { ImageServiceService } from '../service/image-service.service';
import { Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CdkDropList } from "@angular/cdk/drag-drop";
import { Person } from '../models/person.model';
import { ImageModel } from '../models/image.model';

@Component({
  selector: 'app-add-resident',
  imports: [RouterLink, CommonModule, FormsModule],
  templateUrl: './add-resident.component.html',
  styleUrl: './add-resident.component.css'
})
export class AddResidentComponent {
  router = inject(Router)
  residentService = inject(ResidentServiceService);
  imageService = inject(ImageServiceService);
  dob = signal<string>('');
  firstName = signal<string>('');
  lastName = signal<string>('');
  roomNo = signal<string>('');
  isWorker = signal<boolean>(false);
  password = signal<string>('');

  selectedRole = signal<string>('');
  selectedImage = signal<string | null>(null);
  imageFile = signal<File | null>(null); 

  onRoleChange(event: any) {  
    const selectedValue = event.target.value;
    this.selectedRole.set(selectedValue);
    this.isWorker.set(selectedValue === 'worker');

    console.log('Rolle geändert zu:', selectedValue);
    console.log('isEmployee:', this.isWorker());
  }

  onImageSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files[0]) {
      const file = input.files[0];
      this.imageFile.set(file);
      
      // Preview erstellen
      const reader = new FileReader();
      reader.onload = (e: any) => {
        this.selectedImage.set(e.target.result);
      };
      reader.readAsDataURL(file);
    }
  }

  removeImage() {
    this.selectedImage.set(null);
    this.imageFile.set(null);
  }
  
  addPerson(){
    const personData: PersonDto = {
      firstName: this.firstName(),
      lastName: this.lastName(),
      dob: this.dob(),
      roomNo: this.roomNo(),
      isWorker: this.isWorker(),
      password: this.password()
    };

    this.residentService.postPerson(personData).subscribe({
      next: (createdPerson: any) => {
        console.log('Neue Person hinzugefügt:', createdPerson);
        
        // Wenn ein Bild ausgewählt wurde, hochladen
        if (this.selectedImage() && createdPerson.id) {
          this.uploadImage(createdPerson.id);
        } else {
          this.router.navigate(['/residents']);
        }
      },
      error: error => {
        console.error("Hinzufügen der Person fehlgeschlagen." + error.name);
      }
    });
  }

  uploadImage(personId: number) {
    const base64Image = this.selectedImage()?.split(',')[1];
    if (!base64Image) {
      this.router.navigate(['/residents']);
      return;
    }

    const imageData: ImageModel = {
      personId: personId,
      base64Image: base64Image,
      description: 'Profilbild'
    };

    this.imageService.uploadImage(imageData).subscribe({
      next: () => {
        console.log('Bild erfolgreich hochgeladen');
        this.router.navigate(['/residents']);
      },
      error: error => {
        console.error('Fehler beim Hochladen des Bildes:', error);
        this.router.navigate(['/residents']);
      }
    });
  }
}
