import { HttpClient, HttpHeaders } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { STORY_URL } from '../app.config';
import { ImageModel } from '../models/image.model';

const httpOptions = {
  headers: new HttpHeaders({
    'Content-Type': 'application/json'
  })
}

@Injectable({
  providedIn: 'root'
})
export class ImageService {

  constructor() { }

  private url = inject(STORY_URL);
  private http = inject(HttpClient);

  getImages(){
    return this.http.get<ImageModel[]>(this.url + 'image');
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
}
