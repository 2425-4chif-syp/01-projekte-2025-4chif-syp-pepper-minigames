import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_URL } from './constants';

export interface Resident {
  id?: number;
  Firstname: string;
  Lastname: string;
  DOB?: string;
}

@Injectable({
  providedIn: 'root'
})
export class UserAPIService {
    private apiUrl = `${API_URL}/api/residents`;

    constructor(private http: HttpClient) {}

    getResidents(): Observable<Resident[]> {
      return this.http.get<Resident[]>(this.apiUrl);
    }

    addResident(user: Resident): Observable<Resident> {
      const payload = {
        FirstName: user.Firstname,
        LastName: user.Lastname,
        DOB: user.DOB
      };
    
      return this.http.post<Resident>(this.apiUrl, payload);
    }

    deleteResident(id: number): Observable<void> {
      return this.http.delete<void>(`${this.apiUrl}/${id}`);
    }
  }
