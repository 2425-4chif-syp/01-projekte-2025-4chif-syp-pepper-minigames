import { Component, OnInit } from '@angular/core';
import { FormControl, ReactiveFormsModule, FormsModule } from '@angular/forms';
import { CdkDragDrop, CdkDrag, DragDropModule } from '@angular/cdk/drag-drop';
import { debounceTime, of, forkJoin, distinctUntilChanged } from 'rxjs';
import { switchMap, map, catchError, tap } from 'rxjs/operators';
import { CommonModule } from '@angular/common';

import { CardModule } from 'primeng/card';
import { ButtonModule } from 'primeng/button';
import { TagModule } from 'primeng/tag';
import { ProgressSpinnerModule } from 'primeng/progressspinner';
import { InputTextModule } from 'primeng/inputtext';

import { MenuAPIService } from '../services/menu-api.service';
import { DayPlan } from '../models/day-plan.model';
import { Food } from '../models/food.model';
import { WeekPlan } from '../models/week-plan.model';

@Component({
    selector: 'app-raster',
    imports: [
        CommonModule,
        ReactiveFormsModule,
        FormsModule,
        DragDropModule,
        CardModule,
        ButtonModule,
        TagModule,
        ProgressSpinnerModule,
        InputTextModule,
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
  maxWeek = 4;
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
    private menuApiService: MenuAPIService
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
                  (f) => (f.id ?? f.name) === (food.id ?? food.name)
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
    // Backend nutzt einen 4-Wochen-Zyklus.
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
      alert('CSV-Import wird vom Backend derzeit nicht unterstützt.');
      event.target.value = '';
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
            if (dayPlan[key] === food.name) {
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
          plan.dayPlans.some((day) => Object.values(day).includes(food.name))
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
        this.currentWeekPlan.dayPlans[dayIndex]?.[field] === draggedFood.name
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
    return drag.data && drag.data.type === 'soup';
  }

  mainPredicate(drag: CdkDrag<Food>, _drop: any): boolean {
    return (
      drag.data && (drag.data.type === 'main' || drag.data.type === 'soup')
    );
  }

  dessertPredicate(drag: CdkDrag<Food>, _drop: any): boolean {
    return drag.data && drag.data.type === 'dessert';
  }

  onMainDrop(event: any, day: DayPlan, field: keyof DayPlan) {
    if (event.previousContainer !== event.container) {
      const food = event.item.data as Food;
      day[field] = food.name as any;
      this.triggerAutosave();
    }
  }

  onSoupDrop(event: any, day: DayPlan) {
    const food = event.item.data as Food;
    day.daySoup = food.name as any;
    this.triggerAutosave();
  }

  onDessertDrop(event: any, day: DayPlan) {
    const food = event.item.data as Food;
    day.dessert = food.name as any;
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

  private saveWeekPlan(plan: WeekPlan) {
    const payload = plan.dayPlans.map((d, i) => ({
      weekNumber: this.currentWeek,
      weekDay: i,
      soup: this.getFoodRefByName(d.daySoup),
      lunch1: this.getFoodRefByName(d.menuOne),
      lunch2: this.getFoodRefByName(d.menuTwo),
      lunchDessert: this.getFoodRefByName(d.dessert),
      dinner1: this.getFoodRefByName(d.eveningOne),
      dinner2: this.getFoodRefByName(d.eveningTwo),
    }));
    return this.menuApiService.updateWeekMenu(payload);
  }

  private handleError(error: any, _context: string) {
    if (error.status === 404) {
      return of([]);
    }

    console.error(`Unexpected error in ${_context}:`, error);
    return of([]);
  }

  private getFoodRefByName(name: string): { id: number } | null {
    const id = this.foods.find((f) => f.name === name)?.id ?? 0;
    return id ? { id } : null;
  }

  getFoodTypeLabel(type: string): string {
    switch (type) {
      case 'soup': return 'Suppe';
      case 'main': return 'Hauptgericht';
      case 'dessert': return 'Nachspeise';
      default: return type;
    }
  }

  getFoodTypeSeverity(type: string): 'success' | 'info' | 'warn' | 'danger' | 'secondary' | 'contrast' | undefined {
    switch (type) {
      case 'soup': return 'warn';
      case 'main': return 'success';
      case 'dessert': return 'info';
      default: return 'secondary';
    }
  }
}
