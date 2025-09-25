import { Component, inject, signal, computed } from '@angular/core';
import { Person } from '../models/person.model';
import { ResidentServiceService } from '../service/resident-service.service';
import { error } from 'console';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { PersonDto } from '../models/person-dto.model';

// Erweitere das Person-Interface mit der Memory-Eigenschaft
interface PersonWithMemory extends Person {
  memoryActive: boolean;
}

@Component({
  selector: 'app-residents',
  imports: [FormsModule, CommonModule, RouterLink],
  templateUrl: './residents.component.html',
  styleUrl: './residents.component.css'
})
export class ResidentsComponent {

  residents = signal<Person[]>([]);
  memoryFilterActive = signal<boolean>(false);
  residentService = inject(ResidentServiceService);
  dob = signal<string>('');
  firstName = signal<string>('');
  lastName = signal<string>('');
  roomNo = signal<string>('');
  isWorker = signal<boolean>(false);
  password = signal<string>('');

  ngOnInit() {
    this.getAllResidents();
  }

  getAllResidents(){
    this.residentService.getResidents().subscribe({
      next: data => {
    
        this.residents.set(data);
        console.log(this.residents())
      },
      error: error=> {
        console.error("Laden der Personen fehlgeschlagen." + error.name);
      }
    });
  }

  onMemoryFilterChange(event: Event): void {
    const checked = (event.target as HTMLInputElement).checked;
  }

  deletePerson(id: number){
    this.residentService.deletePerson(id).subscribe({
      next: () => {
        console.log(`Person mit ID ${id} wurde gelöscht.`);
        this.getAllResidents();
        window.location.reload();
      },
      error: error => {
        window.location.reload();
        console.error("Löschen der Person fehlgeschlagen." + error.name);
      }
    });
  }

  
}
