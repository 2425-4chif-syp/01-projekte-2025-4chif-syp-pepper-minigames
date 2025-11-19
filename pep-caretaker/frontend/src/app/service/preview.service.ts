import { HttpClient, HttpHeaders } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { STORY_URL } from '../app.config';
import { Tas } from '../models/tas.model';

const httpOptions = {
  headers: new HttpHeaders({
    'Content-Type': 'application/json'
  })
}

@Injectable({
  providedIn: 'root'
})
export class PreviewService {

  private url = inject(STORY_URL);
  private http = inject(HttpClient);
  constructor() { }
  
  getTagalongStory(id: number){
    return this.http.get<Tas>(this.url + 'tagalongstories/' + id + '/steps', httpOptions);
  }
}
