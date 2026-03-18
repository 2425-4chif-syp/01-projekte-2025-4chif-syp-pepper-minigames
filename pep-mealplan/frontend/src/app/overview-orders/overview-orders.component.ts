import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RadioButtonModule } from 'primeng/radiobutton';
import { CardModule } from 'primeng/card';
import { ButtonModule } from 'primeng/button';
import { DatePickerModule } from 'primeng/datepicker';
import { TableModule } from 'primeng/table';
import { TagModule } from 'primeng/tag';
import { TooltipModule } from 'primeng/tooltip';
import { ProgressSpinnerModule } from 'primeng/progressspinner';
import { ProgressBarModule } from 'primeng/progressbar';

import { MenuAPIService } from '../services/menu-api.service';
import { UserAPIService } from '../services/residents-api.service';
import { OrdersAPIService } from '../services/orders-api.service';
import { DayPlan } from '../models/day-plan.model';
import { PersonalOrderRow } from '../models/personal-order-row.model';
import { Resident } from '../models/resident.model';
import { WeekPlan } from '../models/week-plan.model';

type ViewOption = 'personal' | 'kueche';

@Component({
  selector: 'app-overview-orders',
  templateUrl: './overview-orders.component.html',
  styleUrls: ['./overview-orders.component.scss'],
  standalone: true,
  imports: [
        CommonModule,
        FormsModule,
        RadioButtonModule,
        CardModule,
        ButtonModule,
        DatePickerModule,
        TableModule,
        TagModule,
        TooltipModule,
        ProgressSpinnerModule,
        ProgressBarModule
    ]
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
      private menuApi: MenuAPIService,
      private ordersApi: OrdersAPIService
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

        // Always fetch from backend first, then cache to localStorage
        this.loadPersonalOrdersFromBackend();
      },
      error: (err) => {
        console.error('Fehler beim Laden des WeekPlans:', err);

        this.weekPlan = null;
        this.dayPlan = null;

        // Fallback to localStorage if weekplan fetch fails
        this.buildPersonalOrdersFromLocalStorage();
        this.computeStats();
        this.loading = false;
      }
    });
  }

  private loadPersonalOrdersFromBackend(): void {
    this.ordersApi.getOrdersByDate(this.selectedDate).subscribe({
      next: (orders) => {
        this.personalOrders = [];

        if (!this.dayPlan) {
          this.orderedCount = 0;
          this.missingCount = this.residentCount;
          this.resetStats();
          this.loading = false;
          return;
        }

        for (const order of orders) {
          const personId = order.person?.id ?? order.personId;
          const resident = this.residents.find(r => Number(r.id) === Number(personId));
          if (!resident) continue;

          const lunchName: string | null = order.selectedLunch?.name ?? null;
          const dinnerName: string | null = order.selectedDinner?.name ?? null;

          this.personalOrders.push({
            residentId: Number(personId),
            name: `${resident.firstname} ${resident.lastname}`,
            soup: this.dayPlan.daySoup ?? null,
            lunch: lunchName,
            dessert: this.dayPlan.dessert ?? null,
            dinner: dinnerName
          });
        }

        this.orderedCount = this.personalOrders.length;
        this.missingCount = Math.max(0, this.residentCount - this.orderedCount);

        // Cache to localStorage for offline use
        this.syncOrdersToLocalStorage(orders);

        this.computeStats();
        this.loading = false;
      },
      error: (err) => {
        console.error('Fehler beim Laden der Bestellungen vom Backend:', err);

        // Fallback to localStorage if backend fails
        this.buildPersonalOrdersFromLocalStorage();
        this.computeStats();
        this.loading = false;
      }
    });
  }

  private syncOrdersToLocalStorage(backendOrders: any[]): void {
    const storage = this.loadStoredSelections();
    const weekKey = this.weekStartIso;

    if (!storage[weekKey]) storage[weekKey] = {};

    for (const order of backendOrders) {
      const personId = order.person?.id ?? order.personId;
      const residentId = String(personId);

      if (!storage[weekKey][residentId]) storage[weekKey][residentId] = {};

      // Determine which menu options were selected
      const lunchName = order.selectedLunch?.name ?? null;
      const dinnerName = order.selectedDinner?.name ?? null;

      let selectedMenu: 'one' | 'two' | null = null;
      let selectedEvening: 'one' | 'two' | null = null;

      if (this.dayPlan) {
        if (lunchName === this.dayPlan.menuOne) selectedMenu = 'one';
        else if (lunchName === this.dayPlan.menuTwo) selectedMenu = 'two';

        if (dinnerName === this.dayPlan.eveningOne) selectedEvening = 'one';
        else if (dinnerName === this.dayPlan.eveningTwo) selectedEvening = 'two';
      }

      storage[weekKey][residentId][String(this.dayIndex)] = {
        selectedMenu,
        selectedEvening
      };
    }

    localStorage.setItem(this.STORAGE_KEY, JSON.stringify(storage));
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
      const name = `${r.firstname} ${r.lastname}`;

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
    const year = date.getFullYear();
    const month = (`0${date.getMonth() + 1}`).slice(-2);
    const day = (`0${date.getDate()}`).slice(-2);
    return `${year}-${month}-${day}`;
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
