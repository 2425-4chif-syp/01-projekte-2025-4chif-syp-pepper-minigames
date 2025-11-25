import { Component, OnInit } from '@angular/core';
import { DayPlan, Food, WeekPlan, MenuAPIService } from '../menu-api.service';
import { CdkDrag, CdkDragDrop } from '@angular/cdk/drag-drop';
import { FormControl } from '@angular/forms';
import { debounceTime, distinctUntilChanged } from 'rxjs';
import { tap } from 'rxjs/operators';
import { FileSaverService } from 'ngx-filesaver';

@Component({
    selector: 'app-week-plan-management',
    templateUrl: './week-plan-management.component.html',
    styleUrls: ['./week-plan-management.component.scss'],
    standalone: false
})
export class WeekPlanManagementComponent implements OnInit {
  WEEK_DAYS = ['MO','DI','MI','DO','FR','SA','SO'];
  selectedDate: string;
  filteredFood: Food[] = [];
  nameFilter = new FormControl('');
  foods: Food[] = [];
  currentWeekPlan: WeekPlan = {} as WeekPlan;
  currentWeekPlanCopy: WeekPlan = {} as WeekPlan;
  specialDayPlans: DayPlan[] = [];
  isDropAllowed: boolean = true;
  foodsLoading = true;
  weekPlanLoading = true;
  searchLoading = false;
  get isLoading(): boolean {
    return this.foodsLoading || this.weekPlanLoading;
  }

  // Timer für den Auto-Save Debounce
  private autoSaveTimer: any;

  constructor(
    private menuApiService: MenuAPIService,
    private fileSaverService: FileSaverService
  ) {
    let thisWeeksMonday = new Date();
    // Berechne den Montag der aktuellen Woche (bei Sonntag wird auf die Vorwoche korrigiert)
    thisWeeksMonday.setDate(
      thisWeeksMonday.getDate() - thisWeeksMonday.getDay() + (thisWeeksMonday.getDay() === 0 ? -6 : 1)
    );
    this.selectedDate = thisWeeksMonday.toISOString().split('T')[0];
  }

  ngOnInit(): void {
    // Lade die verfügbaren Foods
    this.menuApiService.getFood().subscribe((data) => {
      this.foods = data ? data : [];
      this.foods.sort((a, b) => a.Name.localeCompare(b.Name));
      this.filteredFood = this.foods;
      this.foodsLoading = false;
    });

    this.nameFilter.valueChanges
      .pipe(
        debounceTime(200),
        distinctUntilChanged(),
        tap(() => (this.searchLoading = true))
      )
      .subscribe((nameFilter: string | null) => {
        this.filteredFood = this.foods.filter((food) =>
          food.Name.toLowerCase().includes(nameFilter ? nameFilter.toLowerCase() : '')
        );
        this.searchLoading = false;
      });

    // Lade den Wochenplan vom Server
    let date = new Date(this.selectedDate);
    this.menuApiService.getWeekPlan(date).subscribe((data) => {
      this.currentWeekPlan = data;
      console.log("Aktueller Wochenplan:", this.currentWeekPlan); //DEV
      // Speichere eine Kopie zum Vergleichen für den Auto-Save
      this.currentWeekPlanCopy = JSON.parse(JSON.stringify(this.currentWeekPlan));
      this.weekPlanLoading = false;
    });
  }

  // ========================
  // Word Export
  // ========================
  exportAsWord(): void {
    this.menuApiService.getExportedWord(this.selectedDate).subscribe(response => {
      const contentDisposition = response.headers.get('content-disposition');
      console.log('Content-Disposition Header:', contentDisposition); 
      let filename = "Speiseplan.docx"; // Fallback

      if (contentDisposition) {
        const filenameRegex = /filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/;
        const matches = filenameRegex.exec(contentDisposition);
        if (matches != null && matches[1]) {
          filename = matches[1].replace(/['"]/g, '');
        }
      }

      this.fileSaverService.save(response.body, filename);
    }, error => {
      console.error("Fehler beim Word-Export:", error);
      alert("Fehler beim Herunterladen des Word-Dokuments!");
    });
  }

  // Aktualisiert den Wochenplan beim Ändern des Datums
  dateChange(): void {
    this.specialDayPlans = [];
    let date = new Date(this.selectedDate);
    date.setHours(12, 0, 0, 0);
    date.setDate(date.getDate() - date.getDay() + (date.getDay() === 0 ? -6 : 1));

    this.weekPlanLoading = true;
    this.menuApiService.getWeekPlan(date).subscribe((data) => {
      this.currentWeekPlan = data;
      this.currentWeekPlanCopy = JSON.parse(JSON.stringify(this.currentWeekPlan));
      this.weekPlanLoading = false;
    });
    
    this.selectedDate = date.toISOString().split('T')[0];
  }

  addSpecialDayPlans(dateString: string): number {
    let counter = 0;
    let addedPlans = 0;

    for (let dayPlan of this.currentWeekPlanCopy.dayPlans) {
      if (
        dayPlan.daySoup !== this.currentWeekPlan.dayPlans[counter].daySoup ||
        dayPlan.menuOne !== this.currentWeekPlan.dayPlans[counter].menuOne ||
        dayPlan.menuTwo !== this.currentWeekPlan.dayPlans[counter].menuTwo ||
        dayPlan.eveningOne !== this.currentWeekPlan.dayPlans[counter].eveningOne ||
        dayPlan.eveningTwo !== this.currentWeekPlan.dayPlans[counter].eveningTwo
      ) {
        this.currentWeekPlan.dayPlans[counter].date = new Date(new Date(dateString).getTime() + counter * 24 * 60 * 60 * 1000);
        if (this.specialDayPlans.filter((specialDayPlan) => specialDayPlan.date.toISOString() === this.currentWeekPlan.dayPlans[counter].date.toISOString()).length === 0) {
          addedPlans++;
          this.specialDayPlans.push(this.currentWeekPlan.dayPlans[counter]);
        }
      }
      counter++;
    }
    return addedPlans;
  }

  dateCleaned(index: number): string {
    let dateForDay = new Date(this.selectedDate);
    dateForDay.setDate(dateForDay.getDate() + index);
    let day = dateForDay.getDate();
    let month = dateForDay.getMonth() + 1;
    const dayString = day < 10 ? '0' + day : day.toString();
    const monthString = month < 10 ? '0' + month : month.toString();
    return dayString + '. ' + monthString;
  }

  changeDate(amount: number): void {
    let newDate = new Date(this.selectedDate);
    newDate.setDate(newDate.getDate() + amount);
    this.selectedDate = newDate.toISOString().split('T')[0];
    this.dateChange();
  }
  
  resetDate(): void {
    let thisWeeksMonday = new Date();
    thisWeeksMonday.setDate(
      thisWeeksMonday.getDate() - thisWeeksMonday.getDay() + (thisWeeksMonday.getDay() === 0 ? -6 : 1)
    );
    this.selectedDate = thisWeeksMonday.toISOString().split('T')[0];
    this.dateChange();
  }

  // ========================
  // Drag & Drop Handler
  // ========================

  dropFoodInFoods(event: CdkDragDrop<HTMLElement>): void {
    const food = this.foods[event.previousIndex];
    this.foods.splice(event.previousIndex, 1);
    this.foods.splice(event.currentIndex, 0, food);
  }

  onSoupDrop(event: CdkDragDrop<Food>, dayPlan: any): void {
    dayPlan.daySoup = event.item.data.Name;
    this.triggerAutoSave();
  }

  onMainDrop(event: CdkDragDrop<Food>, dayPlan: any, field: keyof any): void {
    dayPlan[field] = event.item.data.Name;
    this.triggerAutoSave();
  }

  onDessertDrop(event: CdkDragDrop<Food>, dayPlan: any): void {
    dayPlan.dessert = event.item.data.Name;
    this.triggerAutoSave();
  }

  onDragEntered(event: any): void {
    const containerEl = event.container.element.nativeElement;
    let allowedType: string = '';
  
    if (containerEl.classList.contains('soup-card')) {
      allowedType = 'soup';
    } else if (containerEl.classList.contains('dessert-card')) {
      allowedType = 'dessert';
    } else if (containerEl.classList.contains('menu-card') || containerEl.classList.contains('evening-card')) {
      allowedType = 'main';
    }
  
    const dragType = event.item.data.Type;
  
    if (allowedType && dragType !== allowedType) {
      containerEl.style.cursor = 'not-allowed';
    } else {
      containerEl.style.cursor = '';
    }
  }
  
  onDragExited(event: any): void {
    event.container.element.nativeElement.style.cursor = '';
  }

  // Predicate-Funktionen für Drag & Drop
  soupPredicate(drag: CdkDrag<Food>, drop: any): boolean {
    return drag.data && drag.data.Type === 'soup';
  }

  mainPredicate(drag: CdkDrag<Food>, drop: any): boolean {
    return drag.data && (drag.data.Type === 'main' || drag.data.Type === 'soup'); // Suppen können auch als Hauptgericht (Abend) verwendet werden
  }

  dessertPredicate(drag: CdkDrag<Food>, drop: any): boolean {
    return drag.data && drag.data.Type === 'dessert';
  }

  // ================================
  // Auto-Save Implementation
  // ================================

  // Startet einen Debounce-Timer (1 Sekunde) nach jeder Änderung
  triggerAutoSave(): void {
    if (this.autoSaveTimer) {
      clearTimeout(this.autoSaveTimer);
    }
    this.autoSaveTimer = setTimeout(() => {
      this.autoSaveChanges();
    }, 1000);
  }

  // Vergleicht den aktuellen Plan mit der gespeicherten Kopie
  hasChanges(): boolean {
    if (!this.currentWeekPlanCopy.dayPlans || !this.currentWeekPlan.dayPlans) {
      return false;
    }
    for (let i = 0; i < this.currentWeekPlan.dayPlans.length; i++) {
      const current = this.currentWeekPlan.dayPlans[i];
      const original = this.currentWeekPlanCopy.dayPlans[i];
      if (
        current.daySoup !== original.daySoup ||
        current.menuOne !== original.menuOne ||
        current.menuTwo !== original.menuTwo ||
        current.dessert !== original.dessert ||
        current.eveningOne !== original.eveningOne ||
        current.eveningTwo !== original.eveningTwo
      ) {
        return true;
      }
    }
    return false;
  }

  // Sendet den aktualisierten Wochenplan an den Server, wenn Änderungen festgestellt wurden
  autoSaveChanges(): void {
    if (!this.hasChanges()) {
      return;
    }
    const baseDate = new Date(this.selectedDate);
    this.currentWeekPlan.dayPlans.forEach((day, idx) => {
      const original = this.currentWeekPlanCopy.dayPlans[idx];
      if (
        day.daySoup !== original.daySoup ||
        day.menuOne !== original.menuOne ||
        day.menuTwo !== original.menuTwo ||
        day.dessert !== original.dessert ||
        day.eveningOne !== original.eveningOne ||
        day.eveningTwo !== original.eveningTwo
      ) {
        const date = new Date(baseDate);
        date.setDate(date.getDate() + idx);
        const payload = {
          SoupID: this.getFoodIdByName(day.daySoup),
          M1ID: this.getFoodIdByName(day.menuOne),
          M2ID: this.getFoodIdByName(day.menuTwo),
          LunchDessertID: this.getFoodIdByName(day.dessert),
          A1ID: this.getFoodIdByName(day.eveningOne),
          A2ID: this.getFoodIdByName(day.eveningTwo)
        };
        const dateStr = date.toISOString().split('T')[0];
        this.menuApiService.updateSpecialMeal(dateStr, payload).subscribe({
          next: () => {
            console.log(`Successfully updated special meal for date: ${dateStr}`);
          },
          error: (err) => {
            console.error(`Failed to update special meal for date: ${dateStr}`, err);
          }
        });
      }
    });
    this.currentWeekPlanCopy = JSON.parse(JSON.stringify(this.currentWeekPlan));
  }

  // Erstellt den Payload für den /api/menu/week Endpoint
  prepareWeekMenuPayload(): any {
    const weekNumber = this.getWeekNumber(new Date(this.selectedDate));
    const menus = this.currentWeekPlan.dayPlans.map((dayPlan, index) => {
      return {
        WeekDay: index, // 0 = Montag, 1 = Dienstag, etc.
        SoupID: this.getFoodIdByName(dayPlan.daySoup),
        M1ID: this.getFoodIdByName(dayPlan.menuOne),
        M2ID: this.getFoodIdByName(dayPlan.menuTwo),
        LunchDessertID: this.getFoodIdByName(dayPlan.dessert),
        A1ID: this.getFoodIdByName(dayPlan.eveningOne),
        A2ID: this.getFoodIdByName(dayPlan.eveningTwo)
      };
    });
    return { WeekNumber: weekNumber, menus: menus };
  }

  // Berechnet die ISO-Wochenzahl aus einem Datum
  getWeekNumber(date: Date): number {
    const copy = new Date(date.getTime());
    copy.setHours(0, 0, 0, 0);
    // Der Donnerstag in der aktuellen Woche bestimmt die Wochenzahl
    copy.setDate(copy.getDate() + 3 - ((copy.getDay() + 6) % 7));
    const week1 = new Date(copy.getFullYear(), 0, 4);
    return 1 + Math.round((((copy.getTime() - week1.getTime()) / 86400000) - 3 + ((week1.getDay() + 6) % 7)) / 7);
  }

  // Sucht in der foods-Liste nach dem Food-Objekt anhand des Namens und gibt dessen ID zurück
  getFoodIdByName(name: string): number {
    return this.foods.find(f => f.Name === name)?.ID ?? 0;
  }
}