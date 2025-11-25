import { Component, OnInit } from '@angular/core';
import { FormControl, ReactiveFormsModule, FormsModule } from '@angular/forms';
import { CdkDragDrop, CdkDrag, DragDropModule } from '@angular/cdk/drag-drop';
import { debounceTime, of, forkJoin, distinctUntilChanged, delay, Observable } from 'rxjs';
import { switchMap, map, catchError, tap } from 'rxjs/operators';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { HttpClient } from '@angular/common/http';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

import { DayPlan, Food, MenuAPIService, WeekPlan } from '../menu-api.service';

@Component({
    selector: 'app-raster',
    imports: [
        CommonModule,
        ReactiveFormsModule,
        FormsModule,
        DragDropModule,
        MatButtonModule,
        MatIconModule,
        // TODO: `HttpClientModule` should not be imported into a component directly.
        // Please refactor the code to add `provideHttpClient()` call to the provider list in the
        // application bootstrap logic and remove the `HttpClientModule` import from this component.
        MatTooltipModule,
        MatProgressSpinnerModule,
    ],
    templateUrl: './raster.component.html',
    styleUrls: ['./raster.component.scss'],
    standalone: true,
})
export class RasterComponent implements OnInit {
  WEEK_DAYS = [
    'Montag',
    'Dienstag',
    'Mittwoch',
    'Donnerstag',
    'Freitag',
    'Samstag',
    'Sonntag',
  ];
  currentWeek = 1;
  maxWeek = 6;
  foods: Food[] = [];
  filteredFood: Food[] = [];
  nameFilter = new FormControl('');
  aliasMap: Record<string, string> = {
    nachspeise: 'dessert',
    suppe: 'soup',
    hauptgericht: 'main',
  };
  currentWeekPlan: WeekPlan = { dayPlans: [], cyclePosition: 0 };
  isDropAllowed: boolean = true;
  private autoSaveTimer: any;
  foodsLoading = true;
  weekPlanLoading = true;
  searchLoading = false;
  get isLoading(): boolean {
    return this.foodsLoading || this.weekPlanLoading;
  }

  constructor(
    private menuApiService: MenuAPIService,
    private http: HttpClient
  ) {}

  ngOnInit() {
    this.getAllFoods().subscribe((foods: Food[]) => {
      this.foods = foods;
      this.filteredFood = foods;
      this.foodsLoading = false;
    });

    this.loadWeek();

    this.nameFilter.valueChanges
      .pipe(
        debounceTime(200),
        distinctUntilChanged(),
        tap(() => (this.searchLoading = true)),
        switchMap((val: string | null) => {
          const term = val?.trim() ?? '';
          if (!term) {
            return of(this.foods);
          }

          const typeSearchTerm = this.aliasMap[term.toLowerCase()] ?? term;

          return forkJoin([
            this.menuApiService
              .getFoodsByName(term)
              .pipe(
                catchError((error) => this.handleError(error, 'getFoodsByName'))
              ),
            this.menuApiService
              .getFoodsByType(typeSearchTerm)
              .pipe(
                catchError((error) => this.handleError(error, 'getFoodsByType'))
              ),
          ]).pipe(
            map(([byName, byType]) => {
              const merged = [...byName];
              byType.forEach((food: Food) => {
                const exists = merged.some(
                  (f) => (f.ID ?? f.Name) === (food.ID ?? food.Name)
                );
                if (!exists) {
                  merged.push(food);
                }
              });
              return merged;
            })
          );
        })
      )
      .subscribe((foods: Food[]) => {
        this.filteredFood = foods;
        this.searchLoading = false;
      });

    this.loadCycleLength();
  }

  loadCycleLength() {
    this.http
      .get<{ cycleLength: number }>('http://localhost:3000/api/cycle-length')
      .subscribe((res) => {
        this.maxWeek = res.cycleLength;
      });
  }

  prevWeek() {
    this.currentWeek = this.currentWeek > 1 ? this.currentWeek - 1 : this.maxWeek;
    this.loadWeek();
  }

  nextWeek() {
    this.currentWeek = this.currentWeek < this.maxWeek ? this.currentWeek + 1 : 1;
    this.loadWeek();
  }

  loadWeek() {
    this.weekPlanLoading = true;
    this.getWeekPlan(this.currentWeek).subscribe((plan: WeekPlan) => {
      this.currentWeekPlan = plan;
      this.weekPlanLoading = false;
    });
  }

  saveToLocalStorage() {
    try {
      const serializedPlan = JSON.stringify(this.currentWeekPlan);
      localStorage.setItem('currentWeekPlan', serializedPlan);
    } catch (error) {
      console.error('Error saving to local storage:', error);
    }
  }

  loadFromLocalStorage(): WeekPlan | null {
    try {
      const serializedPlan = localStorage.getItem('currentWeekPlan');
      return serializedPlan ? JSON.parse(serializedPlan) as WeekPlan : null;
    } catch (error) {
      console.error('Error loading from local storage:', error);
      return null;
    }
  }

  dateCleaned(index: number): string {
    const today = new Date();
    today.setDate(today.getDate() - today.getDay() + 1 + index);
    return today.toLocaleDateString();
  }

  onCsvUpload(event: any) {
    const file = event.target.files[0];
    if (file) {
      this.weekPlanLoading = true;
      this.uploadRaster(file).subscribe((plan: WeekPlan) => {
        this.currentWeekPlan = plan;
        this.weekPlanLoading = false;
      });
    }
  }

  tooltip = {
    visible: false,
    x: 0,
    y: 0,
    content: '',
  };

  getUsageTooltipHTML(food: Food): string {
    const matches: string[] = [];

    for (let week = 1; week <= this.maxWeek; week++) {
      const stored = localStorage.getItem(`weekPlan_${week}`);
      if (!stored) continue;

      try {
        const plan: WeekPlan = JSON.parse(stored);
        plan.dayPlans.forEach((dayPlan, index) => {
          const dayName = this.WEEK_DAYS[index];
          for (const key of Object.keys(dayPlan) as (keyof DayPlan)[]) {
            if (dayPlan[key] === food.Name) {
              matches.push(`Woche ${week}, ${dayName}`);
              break;
            }
          }
        });
      } catch {
        continue;
      }
    }

    return matches.length
      ? `<b>Bereits genutzt in:</b><br>${matches
          .map((m) => `• ${m}`)
          .join('<br>')}`
      : '';
  }

  showTooltip(food: Food, event: MouseEvent) {
    const usage = this.getUsageTooltipHTML(food);
    if (!usage) return;

    this.tooltip = {
      visible: true,
      x: event.clientX + 15,
      y: event.clientY + 10,
      content: usage,
    };
  }

  hideTooltip() {
    this.tooltip.visible = false;
  }

  isFoodUsed(food: Food): boolean {
    for (let week = 1; week <= this.maxWeek; week++) {
      const stored = localStorage.getItem(`weekPlan_${week}`);
      if (!stored) continue;

      try {
        const plan: WeekPlan = JSON.parse(stored);
        if (
          plan.dayPlans.some((day) => Object.values(day).includes(food.Name))
        ) {
          return true;
        }
      } catch {
        continue;
      }
    }
    return false;
  }

  dropFoodInFoods(event: CdkDragDrop<Food[]>) {
    const draggedFood = event.item.data as Food;
    const id = event.previousContainer.id;

    const match = id.match(/^drop-(\w+)-(\d+)$/);
    if (match) {
      const field = match[1] as keyof DayPlan;
      const dayIndex = parseInt(match[2], 10);

      if (
        this.currentWeekPlan.dayPlans[dayIndex]?.[field] === draggedFood.Name
      ) {
        this.currentWeekPlan.dayPlans[dayIndex][field] = '' as any;
        this.triggerAutosave();
      }
    }
  }

  onDragEntered(_event: any) {}
  onDragExited(_event: any) {}

  removeFood(day: DayPlan, field: keyof DayPlan) {
    day[field] = '' as any;
    this.triggerAutosave();
  }

  soupPredicate(drag: CdkDrag<Food>, _drop: any): boolean {
    return drag.data && drag.data.Type === 'soup';
  }

  mainPredicate(drag: CdkDrag<Food>, _drop: any): boolean {
    return (
      drag.data && (drag.data.Type === 'main' || drag.data.Type === 'soup')
    );
  }

  dessertPredicate(drag: CdkDrag<Food>, _drop: any): boolean {
    return drag.data && drag.data.Type === 'dessert';
  }

  onMainDrop(event: any, day: DayPlan, field: keyof DayPlan) {
    if (event.previousContainer !== event.container) {
      const food = event.item.data as Food;
      day[field] = food.Name as any;
      this.triggerAutosave();
    }
  }

  onSoupDrop(event: any, day: DayPlan) {
    const food = event.item.data as Food;
    day.daySoup = food.Name as any;
    this.triggerAutosave();
  }

  onDessertDrop(event: any, day: DayPlan) {
    const food = event.item.data as Food;
    day.dessert = food.Name as any;
    this.triggerAutosave();
  }

  triggerAutosave() {
    clearTimeout(this.autoSaveTimer);
    this.autoSaveTimer = setTimeout(() => {
      this.saveWeekPlan(this.currentWeekPlan).subscribe();
    }, 1000);
  }

  private getAllFoods() {
    return this.menuApiService.getFood();
  }

  private getWeekPlan(_week: number) {
    return this.menuApiService.getRasterWeek(_week);
  }

  private uploadRaster(file: File) {
    const reader = new FileReader();
    const obs = new Observable<WeekPlan>((observer) => {
      reader.onload = () => {
        const csv = reader.result as string;
        this.menuApiService.postCSVMenu(csv).subscribe({
          next: () => {
            this.getWeekPlan(this.currentWeek).subscribe((plan) => {
              observer.next(plan);
              observer.complete();
            });
          },
          error: (e) => observer.error(e),
        });
      };
    });
    reader.readAsText(file);
    return obs;
  }

  private saveWeekPlan(plan: WeekPlan) {
    const payload = {
      WeekNumber: this.currentWeek,
      menus: plan.dayPlans.map((d, i) => ({
        WeekDay: i, // 0 = Montag, 1 = Dienstag, etc.
        SoupID: this.getFoodIdByName(d.daySoup),
        M1ID: this.getFoodIdByName(d.menuOne),
        M2ID: this.getFoodIdByName(d.menuTwo),
        LunchDessertID: this.getFoodIdByName(d.dessert),
        A1ID: this.getFoodIdByName(d.eveningOne),
        A2ID: this.getFoodIdByName(d.eveningTwo),
      })),
    };
    return this.menuApiService.updateWeekMenu(payload);
  }

  private handleError(error: any, _context: string) {
    if (error.status === 404) {
      return of([]);
    }

    console.error(`Unexpected error in ${_context}:`, error);
    return of([]);
  }

  private getFoodIdByName(name: string): number {
    return this.foods.find(f => f.Name === name)?.ID ?? 0;
  }
}
