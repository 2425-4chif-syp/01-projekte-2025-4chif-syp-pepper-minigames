import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_URL } from '../constants';
import { Resident } from '../models/resident.model';

@Injectable({
  providedIn: 'root',
})
export class UserAPIService {
  private apiUrl = `${API_URL}/api/residents`;

  constructor(private http: HttpClient) {}

  getResidents(): Observable<Resident[]> {
    return this.http.get<Resident[]>(this.apiUrl);
  }

  addResident(user: Resident): Observable<Resident> {
    const payload = {
      firstname: user.firstname,
      lastname: user.lastname,
      dob: user.dob,
      faceId: user.faceId,
    };

    return this.http.post<Resident>(this.apiUrl, payload);
  }

  deleteResident(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
