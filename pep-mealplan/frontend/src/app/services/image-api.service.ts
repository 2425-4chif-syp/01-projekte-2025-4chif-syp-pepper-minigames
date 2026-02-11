import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_URL } from '../constants';
import { ImageDto, ImageJson, ImageUpload } from '../models/picture.model';

@Injectable({
  providedIn: 'root',
})
export class ImageApiService {
  constructor(private http: HttpClient) {}

  // Get all images with Base64 data
  getAll(): Observable<ImageDto[]> {
    return this.http.get<ImageDto[]>(`${API_URL}/api/images`);
  }

  // Get all images with href URLs (lighter response)
  getAllWithHref(): Observable<ImageJson[]> {
    return this.http.get<ImageJson[]>(`${API_URL}/api/images/pictures`);
  }

  // Get single image by ID
  getById(id: number): Observable<ImageDto> {
    return this.http.get<ImageDto>(`${API_URL}/api/images/${id}`);
  }

  // Get raw image URL for display
  getImageUrl(id: number): string {
    return `${API_URL}/api/images/picture/${id}`;
  }

  // Upload new image
  upload(image: ImageUpload): Observable<ImageDto> {
    return this.http.post<ImageDto>(`${API_URL}/api/images`, image);
  }

  // Delete image
  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${API_URL}/api/images/${id}`);
  }
}
