import { HttpClient } from '@angular/common/http';
import { Inject, Injectable } from '@angular/core';
import { STORY_URL } from './app.config';
import { Observable } from 'rxjs';


export interface Stories {
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
export class StoryService {
  constructor(private httpClient: HttpClient, @Inject(STORY_URL) private baseUrl: string) {
    console.log(baseUrl);
  }

  getTagalongstories(): Observable<Stories[]> {
    return this.httpClient.get<Stories[]>(this.baseUrl);
  }

  postgetTagalongstories(story: Stories): Observable<Stories> {
    return this.httpClient.post<Stories>(this.baseUrl, story);
  }

  getTagalongstory(id: number): Observable<Stories> {
    return this.httpClient.get<Stories>(`${this.baseUrl}/${id}`);
  }

  putTagalongstory(story: Stories): Observable<Stories> {
    return this.httpClient.put<Stories>(`${this.baseUrl}/${story.id}`, story);
  }

  deleteTagalongstory(id: number): Observable<Stories> {
    return this.httpClient.delete<Stories>(`${this.baseUrl}/${id}`);
  }

  postTagalongstorySteps(id: number): Observable<Step> {
    return this.httpClient.post<Step>(`${this.baseUrl}/${id}/steps`, id);
  }

  getTagalongstorySteps(id: number): Observable<Step[]> {
    return this.httpClient.get<Step[]>(`${this.baseUrl}/${id}/steps`);
  }

}
