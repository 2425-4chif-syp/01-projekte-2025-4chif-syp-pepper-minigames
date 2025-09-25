import { inject, Injectable, ɵgetInjectableDef } from '@angular/core';
import { STORY_URL } from '../app.config';
import { HttpClient } from '@angular/common/http';
import { Person } from '../models/person.model';
import { PersonDto } from '../models/person-dto.model';

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

  getResidentById(id: number){
    return this.http.get<Person>(this.BASE_URL + '/' + id)
  }

  deletePerson(id: number){
    return this.http.delete(this.BASE_URL + '/' + id)
  }

  updatePerson(id: number, person: Person){
    return this.http.put<Person>(this.BASE_URL + '/' + id, person)
  }

  postPerson(person: PersonDto){
    return this.http.post(this.BASE_URL, person)
  } 
}
