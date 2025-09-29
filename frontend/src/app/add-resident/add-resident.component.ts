import { Component, inject, signal } from '@angular/core';
import { PersonDto } from '../models/person-dto.model';
import { ResidentServiceService } from '../service/resident-service.service';
import { Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CdkDropList } from "@angular/cdk/drag-drop";
import { Person } from '../models/person.model';

@Component({
  selector: 'app-add-resident',
  imports: [RouterLink, CommonModule, FormsModule],
  templateUrl: './add-resident.component.html',
  styleUrl: './add-resident.component.css'
})
export class AddResidentComponent {
  router = inject(Router)
  residentService = inject(ResidentServiceService);
  dob = signal<string>('');
  firstName = signal<string>('');
  lastName = signal<string>('');
  roomNo = signal<string>('');
  isWorker = signal<boolean>(false);
  password = signal<string>('');

  selectedRole = signal<string>(''); 

  onRoleChange(event: any) {  
    const selectedValue = event.target.value;
    this.selectedRole.set(selectedValue);
    this.isWorker.set(selectedValue === 'worker');

    console.log('Rolle geändert zu:', selectedValue);
    console.log('isEmployee:', this.isWorker());
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
      next: (createdPerson) => {
        console.log('Neue Person hinzugefügt:', createdPerson);
        this.router.navigate(['/residents']); 
      },
      error: error => {
        console.error("Hinzufügen der Person fehlgeschlagen." + error.name);
      }
    });
  }
}
