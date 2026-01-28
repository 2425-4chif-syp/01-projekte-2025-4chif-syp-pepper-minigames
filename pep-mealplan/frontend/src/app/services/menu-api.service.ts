import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map, throwError } from 'rxjs';
import { API_URL } from '../constants';
import { Allergen } from '../models/allergen.model';
import { ApiFood } from '../models/api-food.model';
import { ApiMealPlan } from '../models/api-meal-plan.model';
import { ApiMealPlanUpdate } from '../models/api-meal-plan-update.model';
import { DayPlan } from '../models/day-plan.model';
import { Food } from '../models/food.model';
import { WeekPlan } from '../models/week-plan.model';

@Injectable({
  providedIn: 'root',
})
export class MenuAPIService {
  constructor(private httpClient: HttpClient) {}

  public getWeekPlan(date: Date): Observable<WeekPlan> {
    const weekNumber = this.getCycleWeekNumber(date);
    const monday = this.getIsoMonday(date);

    return this.httpClient
      .get<ApiMealPlan[]>(`${API_URL}/api/menu/week/${weekNumber}`)
      .pipe(map((data) => this.buildWeekPlan(weekNumber, monday, data ?? [])));
  }

  public getRasterWeek(week: number): Observable<WeekPlan> {
    return this.httpClient
      .get<ApiMealPlan[]>(`${API_URL}/api/menu/week/${week}`)
      .pipe(
        map((data) =>
          this.buildWeekPlan(week, this.getIsoMonday(new Date()), data ?? [])
        )
      );
  }

  public getMenuForDate(date: Date): Observable<ApiMealPlan> {
    const weekNumber = this.getCycleWeekNumber(date);
    const weekDay = this.getWeekDayIndex(date);
    return this.httpClient.get<ApiMealPlan>(
      `${API_URL}/api/menu/day/${weekNumber}/${weekDay}`
    );
  }

  public getExportedOrders(date: string): Observable<any[]> {
    return this.httpClient.get<any[]>(`${API_URL}/api/orders/export/${date}`);
  }

  public getFood(): Observable<Food[]> {
    return this.httpClient
      .get<ApiFood[]>(`${API_URL}/api/foods`)
      .pipe(map((foods) => (foods ?? []).map((f) => this.mapFood(f))));
  }

  public getFoodsByName(name: string): Observable<Food[]> {
    const encoded = encodeURIComponent(name);
    return this.httpClient
      .get<ApiFood[]>(`${API_URL}/api/foods/name/${encoded}`)
      .pipe(map((foods) => (foods ?? []).map((f) => this.mapFood(f))));
  }

  public getFoodsByType(type: string): Observable<Food[]> {
    const encoded = encodeURIComponent(type);
    return this.httpClient
      .get<ApiFood[]>(`${API_URL}/api/foods/type/${encoded}`)
      .pipe(map((foods) => (foods ?? []).map((f) => this.mapFood(f))));
  }

  public getFoodById(id: number): Observable<Food> {
    return this.httpClient
      .get<ApiFood>(`${API_URL}/api/foods/${id}`)
      .pipe(map((food) => this.mapFood(food)));
  }

  public addFood(food: Food): Observable<Food> {
    const payload = this.serializeFoodForCreate(food);
    return this.httpClient
      .post<ApiFood>(`${API_URL}/api/foods`, payload)
      .pipe(map((created) => this.mapFood(created)));
  }

  public deleteFood(id: number): Observable<any> {
    return this.httpClient.delete(`${API_URL}/api/foods/${id}`);
  }

  public updateFood(_food: Food): Observable<never> {
    return throwError(() => new Error('Food update is not supported by the API.'));
  }

  public getAllergens(): Observable<Allergen[]> {
    return this.httpClient.get<Allergen[]>(`${API_URL}/api/allergens`);
  }

  public updateWeekMenu(plans: ApiMealPlanUpdate[]): Observable<any> {
    return this.httpClient.post(`${API_URL}/api/menu/week`, plans);
  }

  // -------------------------
  // Mapping helpers
  // -------------------------

  private mapFood(food: ApiFood | null | undefined): Food {
    const allergenCodes =
      food?.allergens
        ?.map((fa) => fa.allergen?.shortname ?? fa.id?.allergenShortname)
        .filter((code): code is string => !!code) ?? [];

    return {
      id: food?.id,
      name: food?.name ?? '',
      type: food?.type ?? '',
      allergens: allergenCodes,
      picture: food?.picture ?? null,
    };
  }

  private serializeFoodForCreate(food: Food): { name: string; type: string; pictureId?: number } {
    return {
      name: food.name,
      type: food.type,
      pictureId: food.picture?.id ?? undefined,
    };
  }

  private buildWeekPlan(
    weekNumber: number,
    monday: Date,
    items: ApiMealPlan[]
  ): WeekPlan {
    const dayPlans: DayPlan[] = Array.from({ length: 7 }, (_, index) => {
      const dayDate = new Date(monday);
      dayDate.setDate(monday.getDate() + index);
      return {
        date: dayDate,
        daySoup: '',
        menuOne: '',
        menuTwo: '',
        dessert: '',
        eveningOne: '',
        eveningTwo: '',
      };
    });

    (items ?? []).forEach((item) => {
      const idx = item.weekDay;
      if (idx < 0 || idx > 6) return;

      dayPlans[idx] = {
        date: dayPlans[idx].date,
        daySoup: item.soup?.name ?? '',
        menuOne: item.lunch1?.name ?? '',
        menuTwo: item.lunch2?.name ?? '',
        dessert: item.lunchDessert?.name ?? '',
        eveningOne: item.dinner1?.name ?? '',
        eveningTwo: item.dinner2?.name ?? '',
      };
    });

    return {
      dayPlans,
      cyclePosition: weekNumber,
    };
  }

  private getWeekDayIndex(date: Date): number {
    const d = this.toUtcDateOnly(date);
    return (d.getUTCDay() + 6) % 7;
  }

  private getCycleWeekNumber(date: Date): number {
    const isoWeek = this.getIsoWeek(date);
    return ((isoWeek - 1) % 4) + 1;
  }

  private getIsoMonday(date: Date): Date {
    const d = this.toUtcDateOnly(date);
    const day = d.getUTCDay() || 7;
    d.setUTCDate(d.getUTCDate() - (day - 1));
    return new Date(d.getUTCFullYear(), d.getUTCMonth(), d.getUTCDate());
  }

  private getIsoWeek(date: Date): number {
    const d = this.toUtcDateOnly(date);
    const day = d.getUTCDay() || 7;
    d.setUTCDate(d.getUTCDate() + 4 - day);
    const yearStart = new Date(Date.UTC(d.getUTCFullYear(), 0, 1));
    const diffDays = Math.floor((d.getTime() - yearStart.getTime()) / 86400000);
    return Math.ceil((diffDays + 1) / 7);
  }

  private toUtcDateOnly(date: Date): Date {
    return new Date(Date.UTC(date.getFullYear(), date.getMonth(), date.getDate()));
  }
}
