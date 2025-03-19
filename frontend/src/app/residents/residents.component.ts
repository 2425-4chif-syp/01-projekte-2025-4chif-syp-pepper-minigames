import { Component, inject, signal } from '@angular/core';
import { Person } from '../models/person.model';
import { ResidentServiceService } from '../service/resident-service.service';
import { error } from 'console';

@Component({
  selector: 'app-residents',
  imports: [],
  templateUrl: './residents.component.html',
  styleUrl: './residents.component.css'
})
export class ResidentsComponent {

  residents = signal<Person[]>([])
  residentService = inject(ResidentServiceService)

  getAllResidents(){
    this.residentService.getResidents().subscribe({
      next: data => {
        this.residents.set(data)
      },
      error: error=>
        {"Laden der Personen fehlgeschlagen." 
          + error.name}
    })
  }
}
