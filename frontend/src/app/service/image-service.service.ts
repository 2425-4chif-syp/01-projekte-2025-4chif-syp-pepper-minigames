import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { STORY_URL } from '../app.config';
import { Person } from '../models/person.model';
import { Image } from '../models/image.model';

@Injectable({
  providedIn: 'root'
})
export class ImageServiceService {

  constructor() { }

  private url = inject(STORY_URL);
  private http = inject(HttpClient);
  
  getImages(){
    return this.http.get<Image[]>(this.url + 'image');
  }
}
