import { Component, inject, signal, computed } from '@angular/core';
import { Person } from '../models/person.model';
import { ResidentServiceService } from '../service/resident-service.service';
import { error } from 'console';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

// Erweitere das Person-Interface mit der Memory-Eigenschaft
interface PersonWithMemory extends Person {
  memoryActive: boolean;
}

@Component({
  selector: 'app-residents',
  imports: [FormsModule, CommonModule],
  templateUrl: './residents.component.html',
  styleUrl: './residents.component.css'
})
export class ResidentsComponent {

  residents = signal<PersonWithMemory[]>([]);
  memoryFilterActive = signal<boolean>(false);
  residentService = inject(ResidentServiceService);

  constructor() {
    this.getAllResidents();
  }

  // Computed property zum Filtern der Bewohner
  filteredResidents = computed(() => {
    if (!this.memoryFilterActive()) {
      return this.residents();
    }
    return this.residents().filter(resident => resident.memoryActive);
  });

  getAllResidents(){
    this.residentService.getResidents().subscribe({
      next: data => {
        // Erweitere die Person-Objekte um die memoryActive-Eigenschaft
        const personsWithMemory = data.map(person => ({
          ...person,
          memoryActive: Math.random() > 0.5 // Zufällige Memory-Aktivierung für das Beispiel
        }));
        this.residents.set(personsWithMemory);
      },
      error: error=> {
        console.error("Laden der Personen fehlgeschlagen." + error.name);
      }
    });
  }

  onMemoryFilterChange(event: Event): void {
    const checked = (event.target as HTMLInputElement).checked;
    //this.memoryFilter = checked;
}
}
