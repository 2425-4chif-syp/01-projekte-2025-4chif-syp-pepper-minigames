import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { API_URL } from './constants';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { HttpResponse } from '@angular/common/http';

export interface DayPlan {
  date: Date;
  daySoup: string;
  menuOne: string;
  menuTwo: string;
  dessert: string;
  eveningOne: string;
  eveningTwo: string;
}

export interface WeekPlan {
  dayPlans: DayPlan[];
  cyclePosition: number;
}

export interface WeekCycle {
  id: number;
  morningSnack: string;
  afternoonSnack: string;
  lateSnack: string;
  weekPlans: WeekPlan[];
  isActive: boolean;
}

export interface Food {
  ID?: number;
  Name: string;
  Allergens: string[];
  Type: string;
  Picture: Picture;
}

export interface Picture {
  Base64: string;
  Name: string;
  MediaType: string
}

export interface Allergen {
  Shortname: string;
  Fullname: string;
}

@Injectable({
  providedIn: 'root'
})
export class MenuAPIService {
  constructor(private httpClient: HttpClient) { }

  public postWeekCycle(weekCycle: WeekCycle): Observable<any> { //Change
    return this.httpClient.post(API_URL + '/week-cycles/', weekCycle);
  }

  public changeActiveCycle(id: number): Observable<any> { //Change
    return this.httpClient.patch(API_URL + '/week-cycles/' + id + '/change-active', {});
  }

  public getWeekCycle(): Observable<WeekCycle> {
    return this.httpClient.get<WeekCycle>(API_URL + '/week-cycles/active'); //Change
  }

  public postSpecialDayPlans(dayPlans: DayPlan[]): Observable<any> { //Change
    return this.httpClient.post(API_URL + '/dayplans/special', dayPlans);
  }

  // Get the WeekPlan including special meals from the API
  public getWeekPlan(date: Date): Observable<WeekPlan> {
    const dateString = date.toISOString().split('T')[0];
    return this.httpClient.get<any[]>(API_URL + '/api/menu/weekWithSpecials/' + dateString).pipe(
      map(data => {
        // Sortieren nach WeekDay (in der API als String, z.B. "0" für Montag)
        const sortedDays = data.sort((a, b) => Number(a.WeekDay) - Number(b.WeekDay));
        // Mapping der API-Response in unser DayPlan-Format
        const dayPlans: DayPlan[] = sortedDays.map((item: any, index: number) => ({
          date: new Date(new Date(date).getTime() + index * 24 * 60 * 60 * 1000),
          daySoup: item.Soup?.Name || '',
          menuOne: item.Lunch1?.Name || '',
          menuTwo: item.Lunch2?.Name || '',
          dessert: item.LunchDessert?.Name || '',
          eveningOne: item.Dinner1?.Name || '',
          eveningTwo: item.Dinner2?.Name || ''
        }));
        return { dayPlans, cyclePosition: 0 } as WeekPlan;
      })
    );
  }

  // Get the raster week plan (without specials) by week number
  public getRasterWeek(week: number): Observable<WeekPlan> {
    return this.httpClient.get<any[]>(API_URL + '/api/menu/week/' + week).pipe(
      map(data => {
        const sorted = data.sort((a, b) => Number(a.WeekDay) - Number(b.WeekDay));
        const dayPlans: DayPlan[] = sorted.map((item: any) => ({
          date: new Date(),
          daySoup: item.Soup?.Name || '',
          menuOne: item.Lunch1?.Name || '',
          menuTwo: item.Lunch2?.Name || '',
          dessert: item.LunchDessert?.Name || '',
          eveningOne: item.Dinner1?.Name || '',
          eveningTwo: item.Dinner2?.Name || ''
        }));
        return { dayPlans, cyclePosition: week } as WeekPlan;
      })
    );
  }

  // Create or update a special meal for a specific date
  public updateSpecialMeal(date: string, payload: any): Observable<any> {
    return this.httpClient.put(`${API_URL}/api/special-meals/${date}`, payload);
  }

  // Get the Word export for the selected week
  public getExportedWord(date: string): Observable<HttpResponse<Blob>> {
    return this.httpClient.get(`${API_URL}/api/menu-export/${date}`, {
      observe: 'response',
      responseType: 'blob'
    });
  }

  // Get all Foods from the API
  public getFood(): Observable<Food[]> {
    return this.httpClient.get<Food[]>(API_URL + '/api/foods');
  }

  // Get foods matching a name (partial or strict)
  public getFoodsByName(name: string, strict: boolean = false): Observable<Food[]> {
    const param = strict ? '?strict=true' : '';
    const encoded = encodeURIComponent(name);
    return this.httpClient.get<Food[]>(`${API_URL}/api/foods/name/${encoded}${param}`);
  }

  // Get foods of a specific type
  public getFoodsByType(type: string): Observable<Food[]> {
    const encoded = encodeURIComponent(type);
    return this.httpClient.get<Food[]>(`${API_URL}/api/foods/type/${encoded}`);
  }

  // Get a Food by ID from the API
  public getFoodById(id: number): Observable<Food> {
    return this.httpClient.get<Food>(API_URL + '/api/foods/id/' + id);
  }

  // Add a new Food to the API
  public addFood(food: Food): Observable<any> {
    return this.httpClient.post<Food>(API_URL + '/api/foods/', food);
  }

  // Delete a Food by ID from the API
  public deleteFood(id: number): Observable<any> {
    return this.httpClient.delete(API_URL + '/api/foods/' + id);
  }

  // Update a Food by ID from the API
  public updateFood(food: Food): Observable<any> {
    return this.httpClient.patch(API_URL + '/api/foods/' + food.ID, food);
  }

  // Get all Allergens from the API
  public getAllergens(): Observable<Allergen[]> {
    return this.httpClient.get<Allergen[]>(API_URL + '/api/allergens/');
  }

  // Update the WeekPlan for a week
  updateWeekMenu(payload: any): Observable<any> {
    return this.httpClient.post(API_URL + '/api/menu/week', payload);
  }

  /**
   * Sendet den CSV-Content an den Server zum Importieren der Menüs.
   */
  public postCSVMenu(csvContent: string): Observable<any> {
    return this.httpClient.post(API_URL + '/api/menu/csv', { csv: csvContent });
  }

  public getExportedPDF(dateString: string): Observable<Blob> {
    return this.httpClient.get(API_URL + '/week-plans/pdf?dateString=' + dateString, { responseType: 'blob' }); //Change
  }
}
