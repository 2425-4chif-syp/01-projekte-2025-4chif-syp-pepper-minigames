import { HttpClient, HttpHeaders } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { TagAlongStory } from '../models/tag-along-story.model';
import { STORY_URL } from '../app.config';

const httpOptions = {
  headers: new HttpHeaders({
    'Content-Type': 'application/json'
  })
}

@Injectable({
  providedIn: 'root'
})
export class FetchUrlsService {

  private base_url = inject(STORY_URL);
  private http = inject(HttpClient)
  constructor() { }

  postStory(tagAlongStory: TagAlongStory){
    return this.http.post(this.base_url, tagAlongStory, httpOptions);
  }
}
