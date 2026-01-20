import {Injectable} from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs';
import {map} from 'rxjs/operators';
import {API_URL} from './constants';

export interface DayPlan {
    date: Date;
    daySoup: string;
    menuOne: string;
    menuTwo: string;
    dessert: string;
    eveningOne: string;
    eveningTwo: string;

    selectedMenu?: 'one' | 'two' | null;
    selectedEvening?: 'one' | 'two' | null;
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

export interface Picture {
    Base64: string;
    Name: string;
    MediaType: string;
}

export interface Food {
    ID?: number;
    Name: string;
    Allergens: string[];
    Type: string;
    Picture: Picture;
}

export interface Allergen {
    Shortname: string;
    Fullname: string;
}

@Injectable({
    providedIn: 'root',
})
export class MenuAPIService {
    constructor(private httpClient: HttpClient) {
    }

    public postWeekCycle(weekCycle: WeekCycle): Observable<any> {
        return this.httpClient.post(`${API_URL}/week-cycles/`, weekCycle);
    }

    public changeActiveCycle(id: number): Observable<any> {
        return this.httpClient.patch(`${API_URL}/week-cycles/${id}/change-active`, {});
    }

    public getWeekCycle(): Observable<WeekCycle> {
        return this.httpClient.get<WeekCycle>(`${API_URL}/week-cycles/active`);
    }

    public postSpecialDayPlans(dayPlans: DayPlan[]): Observable<any> {
        return this.httpClient.post(`${API_URL}/dayplans/special`, dayPlans);
    }

    // WeekPlan inkl. Special Meals
    public getWeekPlan(date: Date): Observable<WeekPlan> {
        const dateString = date.toISOString().split('T')[0];

        return this.httpClient
            .get<any[]>(`${API_URL}/api/menu/weekWithSpecials/${'2025-11-17'}`)
            .pipe(
                map(data => {
                    const sortedDays = data.sort((a, b) => Number(a.WeekDay) - Number(b.WeekDay));

                    const dayPlans: DayPlan[] = sortedDays.map((item: any, index: number) => ({
                        date: new Date(new Date(date).getTime() + index * 24 * 60 * 60 * 1000),
                        daySoup: item.Soup?.Name || '',
                        menuOne: item.Lunch1?.Name || '',
                        menuTwo: item.Lunch2?.Name || '',
                        dessert: item.LunchDessert?.Name || '',
                        eveningOne: item.Dinner1?.Name || '',
                        eveningTwo: item.Dinner2?.Name || '',
                    }));

                    return {
                        dayPlans,
                        cyclePosition: 0,
                    } as WeekPlan;
                }),
            );
    }

    // Raster-WeekPlan (ohne Specials)
    public getRasterWeek(week: number): Observable<WeekPlan> {
        return this.httpClient.get<any[]>(`${API_URL}/api/menu/week/${week}`).pipe(
            map(data => {
                const sorted = data.sort((a, b) => Number(a.WeekDay) - Number(b.WeekDay));

                const dayPlans: DayPlan[] = sorted.map((item: any) => ({
                    date: new Date(),
                    daySoup: item.Soup?.Name || '',
                    menuOne: item.Lunch1?.Name || '',
                    menuTwo: item.Lunch2?.Name || '',
                    dessert: item.LunchDessert?.Name || '',
                    eveningOne: item.Dinner1?.Name || '',
                    eveningTwo: item.Dinner2?.Name || '',
                }));

                return {
                    dayPlans,
                    cyclePosition: week,
                } as WeekPlan;
            }),
        );
    }

    public getExportedWord(date: string): Observable<HttpResponse<Blob>> {
        return this.httpClient.get(`${API_URL}/api/menu-export/${date}`, {
            observe: 'response',
            responseType: 'blob',
        });
    }

    public getFood(): Observable<Food[]> {
        return this.httpClient.get<Food[]>(`${API_URL}/api/foods`);
    }

    public getFoodsByName(name: string, strict: boolean = false): Observable<Food[]> {
        const param = strict ? '?strict=true' : '';
        const encoded = encodeURIComponent(name);
        return this.httpClient.get<Food[]>(`${API_URL}/api/foods/name/${encoded}${param}`);
    }

    public getFoodsByType(type: string): Observable<Food[]> {
        const encoded = encodeURIComponent(type);
        return this.httpClient.get<Food[]>(`${API_URL}/api/foods/type/${encoded}`);
    }

    public getFoodById(id: number): Observable<Food> {
        return this.httpClient.get<Food>(`${API_URL}/api/foods/id/${id}`);
    }

    public addFood(food: Food): Observable<any> {
        return this.httpClient.post<Food>(`${API_URL}/api/foods/`, food);
    }

    public deleteFood(id: number): Observable<any> {
        return this.httpClient.delete(`${API_URL}/api/foods/${id}`);
    }

    public updateFood(food: Food): Observable<any> {
        return this.httpClient.patch(`${API_URL}/api/foods/${food.ID}`, food);
    }

    public getAllergens(): Observable<Allergen[]> {
        return this.httpClient.get<Allergen[]>(`${API_URL}/api/allergens/`);
    }

    public updateWeekMenu(payload: any): Observable<any> {
        return this.httpClient.post(`${API_URL}/api/menu/week`, payload);
    }

    public postCSVMenu(csvContent: string): Observable<any> {
        return this.httpClient.post(`${API_URL}/api/menu/csv`, {csv: csvContent});
    }
}
