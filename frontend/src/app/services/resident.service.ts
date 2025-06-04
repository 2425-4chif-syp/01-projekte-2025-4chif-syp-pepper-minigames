import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { STORY_URL } from '../app.config';
import { Person } from '../models/person.model';

@Injectable({
  providedIn: 'root'
})
export class ResidentService {

  constructor() { }
  BASE_URL = inject(STORY_URL) + 'person'
  private http = inject(HttpClient)

  getResidents(){
    return this.http.get<Person[]>(this.BASE_URL)
  }
}
