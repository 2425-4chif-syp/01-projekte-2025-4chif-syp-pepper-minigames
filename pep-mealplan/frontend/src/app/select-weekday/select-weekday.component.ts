import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';
import { API_URL } from '../constants';

@Component({
    selector: 'app-select-weekday',
    imports: [CommonModule,
        // TODO: `HttpClientModule` should not be imported into a component directly.
        // Please refactor the code to add `provideHttpClient()` call to the provider list in the
        // application bootstrap logic and remove the `HttpClientModule` import from this component.
        ],
    templateUrl: './select-weekday.component.html',
    styleUrls: ['./select-weekday.component.scss'],
    standalone: true,
})
export class SelectWeekdayComponent implements OnInit {
  daysOfWeek: { date: Date; dayName: string; hasOrder?: boolean }[] = [];
  personName: string = '';
  private userId: number | null = null;

  constructor(private route: ActivatedRoute, private router: Router, private http: HttpClient) {}

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
    const first = encodeURIComponent(parts[0]);
    const last = encodeURIComponent(parts.slice(1).join(' '));
    this.http.get<any[]>(`${API_URL}/api/residents?FirstName=${first}&LastName=${last}`).subscribe(res => {
      if (res.length > 0) {
        const r: any = res[0] as any;
        this.userId = r.id ?? r.ID;
        this.loadOrdersForWeek();
      }
    });
  }

  private loadOrdersForWeek(): void {
    if (!this.userId || this.daysOfWeek.length === 0) return;
    const requests = this.daysOfWeek.map(d =>
      firstValueFrom(this.http.get<any[]>(`${API_URL}/api/orders/date/${d.date.toISOString().split('T')[0]}`))
    );
    Promise.all(requests).then(results => {
      results.forEach((orders, idx) => {
        const has = orders.some(o => o.User && (o.User.ID === this.userId || o.User.id === this.userId));
        this.daysOfWeek[idx].hasOrder = has;
      });
    });
  }

  onDayClick(day: Date) {
    this.router.navigateByUrl(`/select-menu/${this.personName}/${day.toISOString().split('T')[0]}`);
  }

  goBack(): void {
    this.router.navigate(['/create-order']);
  }
}