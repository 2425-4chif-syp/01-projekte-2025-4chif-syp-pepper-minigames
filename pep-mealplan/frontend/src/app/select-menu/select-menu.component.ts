import { Component, OnInit, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, ParamMap } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';
import { API_URL } from '../constants';
import { UserAPIService } from '../services/residents-api.service';
import { MenuAPIService } from '../services/menu-api.service';
import { Resident } from '../models/resident.model';

import { ButtonModule } from 'primeng/button';
import { TagModule } from 'primeng/tag';

@Component({
    selector: 'app-select-menu',
    imports: [CommonModule, ButtonModule, TagModule],
    templateUrl: './select-menu.component.html',
    styleUrls: ['./select-menu.component.scss']
})
export class SelectMenuComponent implements OnInit {
  name: string | null = null;
  rawDate: string | null = null;
  date: string | null = null;
  isNextDisabled: boolean = false;
  isPreviousDisabled: boolean = false;

  selectedSoup: boolean = false;
  selectedMeal: string | null = null;
  selectedDinner: string | null = null;

  menu: any = null;
  userId: number | null = null;

  constructor(
    private route: ActivatedRoute,
    @Inject(Router) private router: Router,
    private http: HttpClient,
    private sanitizer: DomSanitizer,
    private residentApi: UserAPIService,
    private menuApi: MenuAPIService
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe((params: ParamMap) => {
      this.name = params.get('name');
      this.rawDate = params.get('date');
      this.date = this.formatDate(this.rawDate);

      const currentDate = this.getDateFromFormattedString(this.date!);
      this.isNextDisabled = currentDate.getDay() === 0; // Sunday
      this.isPreviousDisabled = currentDate.getDay() === 1; // Monday

      this.loadResident();
      this.loadMenu();
    });
  }

  // Formatieren des Datums für die Anzeige im europäischen Format (Wochentag, DD.MM.YYYY)
  formatDate(dateString: string | null): string | null {
    if (!dateString) return null;

    const dateParts = dateString.split('-'); // Aufteilen in [YYYY, MM, DD]
    const year = parseInt(dateParts[0], 10);
    const month = parseInt(dateParts[1], 10) - 1; // JavaScript-Monate sind 0-basiert
    const day = parseInt(dateParts[2], 10);

    const date = new Date(year, month, day); // Erstelle ein korrektes Date-Objekt
    const weekdays = ['Sonntag', 'Montag', 'Dienstag', 'Mittwoch', 'Donnerstag', 'Freitag', 'Samstag'];
    const weekday = weekdays[date.getDay()];

    // Rückgabe als Wochentag, DD.MM.YYYY
    return `${weekday}, ${this.padZero(day)}.${this.padZero(month + 1)}.${year}`;
  }

  // Hilfsmethode zum Hinzufügen einer führenden Null für Tag/Monat
  padZero(value: number): string {
    return value < 10 ? '0' + value : value.toString();
  }

  private loadResident(): void {
    if (!this.name) return;
    const parts = this.name.split(' ');
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
        this.loadExistingOrder();
      }
    });
  }

  private loadMenu(): void {
    if (!this.rawDate) return;
    const [year, month, day] = this.rawDate.split('-').map(Number);
    const date = new Date(year, month - 1, day);
    this.menuApi.getMenuForDate(date).subscribe((data) => {
      this.menu = data;
      this.loadExistingOrder();
    });
  }

  private loadExistingOrder(): void {
    if (!this.userId || !this.rawDate || !this.menu) return;
    this.http.get<any[]>(`${API_URL}/api/orders/date/${this.rawDate}`).subscribe({
      next: data => {
        const existing = data.find(
          (o) => o.person && o.person.id === this.userId
        );
        if (existing) {
          const lunchId = Number(existing.selectedLunch?.id);
          const dinnerId = Number(existing.selectedDinner?.id);
          if (lunchId && lunchId === Number(this.menu.lunch1?.id)) {
            this.selectedMeal = 'Lunch1';
          } else if (lunchId && lunchId === Number(this.menu.lunch2?.id)) {
            this.selectedMeal = 'Lunch2';
          }
          if (dinnerId && dinnerId === Number(this.menu.dinner1?.id)) {
            this.selectedDinner = 'Dinner1';
          } else if (dinnerId && dinnerId === Number(this.menu.dinner2?.id)) {
            this.selectedDinner = 'Dinner2';
          }
        }else{
          this.selectedMeal = null;
          this.selectedDinner = null;
        }
      },
      error: () => {},
    });
  }

  private saveOrder(): void {
    if (!this.userId || !this.rawDate || !this.menu) return;
    if (!this.selectedMeal || !this.selectedDinner) return;
    const lunch =
      this.selectedMeal === 'Lunch1' ? this.menu.lunch1 : this.menu.lunch2;
    const dinner =
      this.selectedDinner === 'Dinner1' ? this.menu.dinner1 : this.menu.dinner2;
    if (!lunch?.id || !dinner?.id) return;
    const payload = {
      date: this.rawDate,
      person: { id: this.userId },
      selectedLunch: { id: Number(lunch.id) },
      selectedDinner: { id: Number(dinner.id) }
    };
    this.http.put(`${API_URL}/api/orders/by-user-date`, payload).subscribe({
      next: () => this.loadExistingOrder(),
      error: err => console.error('Failed to save order', err)
    });
  }

  imgSrc(food: any): SafeUrl {
    if (!food || !food.picture) return '' as any;
    const url = `data:${food.picture.mediaType};base64,${food.picture.base64}`;
    return this.sanitizer.bypassSecurityTrustUrl(url);
  }

  // Auswahl der Suppe
  selectSoup(): void {
    this.selectedSoup = !this.selectedSoup;
  }

  // Auswahl eines Mittagessens
  selectMeal(meal: string): void {
    this.selectedMeal = this.selectedMeal === meal ? null : meal;
    this.saveOrder();
  }

  // Auswahl eines Abendessens
  selectDinner(dinner: string): void {
    this.selectedDinner = this.selectedDinner === dinner ? null : dinner;
    this.saveOrder();
  }

  // Zurück zur vorherigen Seite
  goBack(): void {
    this.router.navigate(['/order-day', this.name]);
  }

  previousDate(): void {
    const currentDate = this.getDateFromFormattedString(this.date!);
    if (currentDate.getDay() === 1) return;
    currentDate.setDate(currentDate.getDate() - 1);
    const newDate = this.getFormattedDateForUrl(currentDate);
    this.router.navigate(['/order-menu', this.name, newDate]);
  }

  nextDate(): void {
    const currentDate = this.getDateFromFormattedString(this.date!);
    if (currentDate.getDay() === 0) return;
    currentDate.setDate(currentDate.getDate() + 1);
    const newDate = this.getFormattedDateForUrl(currentDate);
    this.router.navigate(['/order-menu', this.name, newDate]);
  }

  getDateFromFormattedString(formattedString: string): Date {
    const dateParts = formattedString.split(', ')[1].split('.');
    const day = parseInt(dateParts[0], 10);
    const month = parseInt(dateParts[1], 10) - 1;
    const year = parseInt(dateParts[2], 10);
    return new Date(year, month, day);
  }

  getFormattedDateForUrl(date: Date): string {
    const year = date.getFullYear();
    const month = ('0' + (date.getMonth() + 1)).slice(-2);
    const day = ('0' + date.getDate()).slice(-2);
    return `${year}-${month}-${day}`;
  }
}
