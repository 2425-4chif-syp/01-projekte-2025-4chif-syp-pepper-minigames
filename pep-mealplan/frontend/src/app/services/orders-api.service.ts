import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_URL } from '../constants';

export interface OrderUpsertPayload {
  date: string;
  personId: number;
  selectedLunchId: number;
  selectedDinnerId: number;
}

@Injectable({
  providedIn: 'root',
})
export class OrdersAPIService {
  private readonly apiUrl = `${API_URL}/api/orders`;

  constructor(private http: HttpClient) {}

  upsertOrder(payload: OrderUpsertPayload): Observable<any> {
    // Use POST endpoint which handles both create and update via the service
    return this.http.post(this.apiUrl, payload);
  }
}
