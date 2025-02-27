import { HttpClient } from '@angular/common/http';
import { Inject, Injectable } from '@angular/core';
import { STORY_URL } from './app.config';
import { Observable } from 'rxjs';


export interface Storys {
  id: number
  name: string
  storyIcon: string
  steps: Step[]
  isEnabled: boolean
}

export interface Step {
  id: number
  text: string
  image: string
  duration: number
  moveNameAndDuration: string
}

@Injectable({
  providedIn: 'root'
})
export class StorysService {
  constructor(private httpClient: HttpClient, @Inject(STORY_URL) private baseUrl: string) { }

  getTagalongstories(): Observable<Storys[]> {
    return this.httpClient.get<Storys[]>(this.baseUrl);
  }

  postgetTagalongstories(story: Storys): Observable<Storys> {
    return this.httpClient.post<Storys>(this.baseUrl, story);
  }

  getTagalongstory(id: number): Observable<Storys> {
    return this.httpClient.get<Storys>(`${this.baseUrl}/${id}`);
  }

  putTagalongstory(story: Storys): Observable<Storys> {
    return this.httpClient.put<Storys>(`${this.baseUrl}/${story.id}`, story);
  }

  deleteTagalongstory(id: number): Observable<Storys> {
    return this.httpClient.delete<Storys>(`${this.baseUrl}/${id}`);
  }

  postTagalongstorySteps(id: number): Observable<Step> {
    return this.httpClient.post<Step>(`${this.baseUrl}/${id}/steps`, id);
  }

  getTagalongstorySteps(id: number): Observable<Step[]> {
    return this.httpClient.get<Step[]>(`${this.baseUrl}/${id}/steps`);
  }

}
