import { inject, Injectable, ɵgetInjectableDef } from '@angular/core';
import { STORY_URL } from '../app.config';
import { HttpClient } from '@angular/common/http';
import { Person } from '../models/person.model';

@Injectable({
  providedIn: 'root'
})
export class ResidentServiceService {

  constructor() { }
  BASE_URL = inject(STORY_URL) + 'person'
  private http = inject(HttpClient)

  getResidents(){
    return this.http.get<Person[]>(this.BASE_URL)
  }
}
