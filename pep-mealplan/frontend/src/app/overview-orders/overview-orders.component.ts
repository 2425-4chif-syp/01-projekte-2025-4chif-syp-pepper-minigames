import { Component, OnInit } from '@angular/core';
import { CommonModule, NgClass } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RadioButtonModule } from 'primeng/radiobutton';

import { UserAPIService, Resident } from '../residents-api.service';
import { MenuAPIService, WeekPlan, DayPlan } from '../menu-api.service';

type ViewOption = 'personal' | 'kueche';

interface PersonalOrderRow {
  residentId: number;
  name: string;
  soup: string | null;
  lunch: string | null;
  dessert: string | null;
  dinner: string | null;
}

@Component({
  selector: 'app-overview-orders',
  templateUrl: './overview-orders.component.html',
  styleUrls: ['./overview-orders.component.scss'],
  standalone: true,
  imports: [CommonModule, FormsModule, RadioButtonModule, NgClass]
})
export class OverviewOrdersComponent implements OnInit {
  private readonly STORAGE_KEY = 'pepper_resident_week_selections';

  viewOption: ViewOption = 'personal';
  selectedDate: string = this.getTodayIso();

  loading = true;

  residents: Resident[] = [];
  residentCount = 0;

  weekStartIso = '';
  weekPlan: WeekPlan | null = null;
  dayIndex = 0;
  dayPlan: DayPlan | null = null;

  personalOrders: PersonalOrderRow[] = [];

  orderedCount = 0;
  missingCount = 0;

  orderStats = {
    Soup: 0,
    Lunch1: 0,
    Lunch2: 0,
    LunchDessert: 0,
    Dinner1: 0,
    Dinner2: 0
  };

  constructor(
      private residentApi: UserAPIService,
      private menuApi: MenuAPIService
  ) {}

  ngOnInit(): void {
    this.loading = true;

    this.residentApi.getResidents().subscribe({
      next: (res: Resident[]) => {
        this.residents = res ?? [];
        this.residentCount = this.residents.length;
        this.refreshForDate();
      },
      error: (err) => {
        console.error(err);
        this.residents = [];
        this.residentCount = 0;
        this.refreshForDate();
      }
    });
  }

  refreshForDate(): void {
    this.loading = true;

    const sel = new Date(this.selectedDate);
    const monday = this.getMonday(sel);

    this.weekStartIso = this.toIsoDate(monday);
    this.dayIndex = this.computeDayIndexFromMonday(monday, sel);

    this.menuApi.getWeekPlan(monday).subscribe({
      next: (wp: WeekPlan) => {
        this.weekPlan = wp ?? null;
        this.dayPlan = this.weekPlan?.dayPlans?.[this.dayIndex] ?? null;

        this.buildPersonalOrdersFromLocalStorage();
        this.computeStats();

        this.loading = false;
      },
      error: (err) => {
        console.error('Fehler beim Laden des WeekPlans:', err);

        this.weekPlan = null;
        this.dayPlan = null;

        this.personalOrders = [];
        this.orderedCount = 0;
        this.missingCount = this.residentCount;

        this.resetStats();
        this.loading = false;
      }
    });
  }

  changeDay(delta: number): void {
    const d = new Date(this.selectedDate);
    d.setDate(d.getDate() + delta);
    this.selectedDate = this.toIsoDate(d);
    this.refreshForDate();
  }

  displaySelectedDate(): string {
    const [y, m, d] = this.selectedDate.split('-');
    return `${d}.${m}.${y}`;
  }

  displaySelectedWeekday(): string {
    const date = new Date(this.selectedDate);
    const days = ['SO', 'MO', 'DI', 'MI', 'DO', 'FR', 'SA'];
    return days[date.getDay()];
  }

  percent(value: number): number {
    const total = Math.max(1, this.orderedCount);
    return Math.min(100, Math.round((value / total) * 100));
  }

  // =========================
  // LOCALSTORAGE -> ROWS
  // =========================

  private buildPersonalOrdersFromLocalStorage(): void {
    this.personalOrders = [];

    if (!this.dayPlan) {
      this.orderedCount = 0;
      this.missingCount = this.residentCount;
      return;
    }

    const storage = this.loadStoredSelections();
    const weekData = storage?.[this.weekStartIso] ?? {};

    for (const r of this.residents) {
      const residentId = Number(r.id);
      const name = `${r.Firstname} ${r.Lastname}`;

      const residentWeek = weekData[String(residentId)];
      const daySel = residentWeek?.[String(this.dayIndex)];

      const selectedMenu: 'one' | 'two' | null = daySel?.selectedMenu ?? null;
      const selectedEvening: 'one' | 'two' | null = daySel?.selectedEvening ?? null;

      // ✅ Anzeigen wenn mind. eines gewählt ist
      if (selectedMenu === null && selectedEvening === null) {
        continue;
      }

      const lunchName =
          selectedMenu === 'one'
              ? this.dayPlan.menuOne ?? null
              : selectedMenu === 'two'
                  ? this.dayPlan.menuTwo ?? null
                  : null;

      const dinnerName =
          selectedEvening === 'one'
              ? this.dayPlan.eveningOne ?? null
              : selectedEvening === 'two'
                  ? this.dayPlan.eveningTwo ?? null
                  : null;

      this.personalOrders.push({
        residentId,
        name,
        soup: this.dayPlan.daySoup ?? null,
        lunch: lunchName,
        dessert: this.dayPlan.dessert ?? null,
        dinner: dinnerName
      });
    }

    this.orderedCount = this.personalOrders.length;
    this.missingCount = Math.max(0, this.residentCount - this.orderedCount);
  }

  private loadStoredSelections(): any {
    const raw = localStorage.getItem(this.STORAGE_KEY);
    if (!raw) return {};
    try {
      return JSON.parse(raw);
    } catch {
      return {};
    }
  }

  // =========================
  // STATS
  // =========================

  private computeStats(): void {
    this.resetStats();

    if (!this.dayPlan) return;

    // Suppe/Dessert: pro "Besteller"
    this.orderStats.Soup = this.orderedCount;
    this.orderStats.LunchDessert = this.orderedCount;

    const lunch1 = this.personalOrders.filter(o => o.lunch === (this.dayPlan?.menuOne ?? null) && !!o.lunch).length;
    const lunch2 = this.personalOrders.filter(o => o.lunch === (this.dayPlan?.menuTwo ?? null) && !!o.lunch).length;

    const dinner1 = this.personalOrders.filter(o => o.dinner === (this.dayPlan?.eveningOne ?? null) && !!o.dinner).length;
    const dinner2 = this.personalOrders.filter(o => o.dinner === (this.dayPlan?.eveningTwo ?? null) && !!o.dinner).length;

    this.orderStats.Lunch1 = lunch1;
    this.orderStats.Lunch2 = lunch2;
    this.orderStats.Dinner1 = dinner1;
    this.orderStats.Dinner2 = dinner2;
  }

  private resetStats(): void {
    this.orderStats = {
      Soup: 0,
      Lunch1: 0,
      Lunch2: 0,
      LunchDessert: 0,
      Dinner1: 0,
      Dinner2: 0
    };
  }

  // =========================
  // DATE HELPERS
  // =========================

  private getTodayIso(): string {
    return this.toIsoDate(new Date());
  }

  private toIsoDate(d: Date): string {
    const date = new Date(d);
    date.setHours(12, 0, 0, 0);
    return date.toISOString().split('T')[0];
  }

  private getMonday(base: Date): Date {
    const d = new Date(base);
    const day = d.getDay();
    const diff = d.getDate() - day + (day === 0 ? -6 : 1);
    d.setDate(diff);
    d.setHours(12, 0, 0, 0);
    return d;
  }

  private computeDayIndexFromMonday(monday: Date, selected: Date): number {
    const m = new Date(monday);
    const s = new Date(selected);
    m.setHours(12, 0, 0, 0);
    s.setHours(12, 0, 0, 0);

    const diffMs = s.getTime() - m.getTime();
    const diffDays = Math.round(diffMs / (1000 * 60 * 60 * 24));
    return Math.max(0, Math.min(6, diffDays));
  }
}
