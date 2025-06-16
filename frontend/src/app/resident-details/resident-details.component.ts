import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute, Params, Router, RouterLink } from '@angular/router';
import { Person } from '../models/person.model';
import { ResidentServiceService } from '../service/resident-service.service';

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
}
