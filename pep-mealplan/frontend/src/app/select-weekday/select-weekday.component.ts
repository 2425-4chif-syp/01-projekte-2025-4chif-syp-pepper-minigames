import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';
import { API_URL } from '../constants';
import { UserAPIService } from '../services/residents-api.service';
import { Resident } from '../models/resident.model';

import { ButtonModule } from 'primeng/button';
import { TagModule } from 'primeng/tag';

@Component({
    selector: 'app-select-weekday',
    imports: [CommonModule, ButtonModule, TagModule],
    templateUrl: './select-weekday.component.html',
    styleUrls: ['./select-weekday.component.scss'],
    standalone: true,
})
export class SelectWeekdayComponent implements OnInit {
  daysOfWeek: { date: Date; dayName: string; hasOrder?: boolean }[] = [];
  personName: string = '';
  private userId: number | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private http: HttpClient,
    private residentApi: UserAPIService
  ) {}

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      this.personName = params.get('name') || 'Unbekannt';
      this.loadResident();
      this.calculateWeek();
    });
  }

  private calculateWeek() {
    const today = new Date();
    const dayOfWeek = today.getDay();
    const startOfNextWeek = new Date(today);
    startOfNextWeek.setDate(today.getDate() - dayOfWeek + 8); // Set to next Monday

    this.daysOfWeek = Array.from({ length: 7 }, (_, i) => {
      const day = new Date(startOfNextWeek);
      day.setDate(startOfNextWeek.getDate() + i);

      const options = { weekday: 'long', day: 'numeric', month: 'numeric' } as const;
      const dayName = day.toLocaleDateString('de-DE', options);

      return { date: day, dayName };
    });
    this.loadOrdersForWeek();
  }

  private loadResident(): void {
    const parts = this.personName.split(' ');
    const first = parts[0]?.trim() ?? '';
    const last = parts.slice(1).join(' ').trim();
    this.residentApi.getResidents().subscribe((res: Resident[]) => {
      const match = res.find(
        (r) =>
          r.firstname.toLowerCase() === first.toLowerCase() &&
          r.lastname.toLowerCase() === last.toLowerCase()
      );
      if (match?.id != null) {
        this.userId = match.id;
        this.loadOrdersForWeek();
      }
    });
  }

  private loadOrdersForWeek(): void {
    if (!this.userId || this.daysOfWeek.length === 0) return;
    const requests = this.daysOfWeek.map(d =>
      firstValueFrom(
        this.http.get<any[]>(
          `${API_URL}/api/orders/date/${this.toIsoDateLocal(d.date)}`
        )
      )
    );
    Promise.all(requests).then(results => {
      results.forEach((orders, idx) => {
        const has = orders.some(
          (o) => o.person && o.person.id === this.userId
        );
        this.daysOfWeek[idx].hasOrder = has;
      });
    });
  }

  onDayClick(day: Date) {
    this.router.navigateByUrl(
      `/order-menu/${this.personName}/${this.toIsoDateLocal(day)}`
    );
  }

  private toIsoDateLocal(date: Date): string {
    const year = date.getFullYear();
    const month = (`0${date.getMonth() + 1}`).slice(-2);
    const day = (`0${date.getDate()}`).slice(-2);
    return `${year}-${month}-${day}`;
  }

  goBack(): void {
    this.router.navigate(['/order']);
  }
}
