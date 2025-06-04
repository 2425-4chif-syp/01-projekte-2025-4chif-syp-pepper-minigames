import { HttpClient, HttpHeaders } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { STORY_URL } from '../app.config';
import { ITagalongStory } from '../models/tagalongstory.model';

const httpOptions = {
  headers: new HttpHeaders({
    'Content-Type': 'application/json'
  })
}

@Injectable({
  providedIn: 'root'
})
export class TagalongstoryService {
  BASE_URL = inject(STORY_URL);
  private http = inject(HttpClient);
  constructor() { }

  getAllTagalongstories() {
    return this.http.get<ITagalongStory[]>(this.BASE_URL+"tagalongstories"+"?v="+Math.random())
  }

  deleteStory(id: number){
    return this.http.delete(this.BASE_URL+"tagalongstories/" + id, httpOptions)
  }

  enablingStory(id: number, isEnabled: boolean){
    return this.http.put("/api/tagalongstories/" + id, {"isEnabled" : isEnabled}, httpOptions)
  }
}
