import { HttpClient, HttpHeaders } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { STORY_URL } from '../app.config';
import { Person } from '../models/person.model';
import { ImageModel } from '../models/image.model';
import { Observable } from 'rxjs';
import { ImageDto } from '../models/imageDto.model';

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
    return this.http.get<ImageDto[]>(this.url + 'image');
  }

  uploadImage(imageDto: ImageModel){
    return this.http.post(this.url + 'image', imageDto, httpOptions);
  }

  getImageBase64(id: number): Observable<string | null> {
    const apiUrl = '/api/tagalongstories/' + id + '/steps';
    return new Observable(observer => {
      this.http.get<any[]>(apiUrl).subscribe(
        response => {

          for (let i = 0;response.length > 0 && response[i].imageBase64;i++ ){
            observer.next(response[i].imageBase64);
          }
          observer.next(null);
          observer.complete();

          /*if (response.length > 0 && response[0].imageBase64) {
            observer.next(response[0].imageBase64);
          } else {
            observer.next(null);
          }
          observer.complete();*/
        },
        error => {
          observer.error(error);
        }
      );
    });
  }

  getTitleImage(id: number): Observable<string | null> {
    const apiUrl = '/api/tagalongstories/' + id + '/image';
    return new Observable(observer => {
      this.http.get(apiUrl, { responseType: 'text' }).subscribe(
        response => {
          if (response) {
            observer.next(response); // Der Base64-String wird direkt zurückgegeben
          } else {
            observer.next(null);
          }
          observer.complete();
        },
        error => {
          observer.error(error);
        }
      );
    });
  }
  
  deleteImage(id: number){
    return this.http.delete("/api/image/" + id, httpOptions)
  }

  deleteStory(id: number){
    return this.http.delete("/api/tagalongstories/" + id, httpOptions)
  }

  enablingStory(id: number, isEnabled: boolean){
    return this.http.put("/api/tagalongstories/" + id, {"isEnabled" : isEnabled}, httpOptions)
  }
}