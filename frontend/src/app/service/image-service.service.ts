import { HttpClient, HttpHeaders } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { STORY_URL } from '../app.config';
import { Person } from '../models/person.model';
import { ImageModel } from '../models/image.model';
import { Observable } from 'rxjs';

const httpOptions = {
  headers: new HttpHeaders({
    'Content-Type': 'application/json'
  })
}

@Injectable({
  providedIn: 'root'
})
export class ImageServiceService {

  constructor() { }

  private url = inject(STORY_URL);
  private http = inject(HttpClient);
  
  getImages(){
    return this.http.get<ImageModel[]>(this.url + 'image');
  }

  uploadImage(imageDto: ImageModel){
    return this.http.post(this.url + 'image', imageDto, httpOptions);
  }
}
