import { Component, inject } from '@angular/core';
import { RouterModule } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { STORY_URL } from '../app.config';
import { ITagalongStory } from '../../models/tagalongstories.model';
import { CommonModule } from '@angular/common';
import { forkJoin } from 'rxjs';
import { FormsModule } from '@angular/forms';
import { ImageServiceService } from '../service/image-service.service';
import e from 'express';

@Component({
  selector: 'app-tagalongstory',
  standalone: true,
  imports: [RouterModule, CommonModule, FormsModule],
  templateUrl: './tagalongstory.component.html',
  styleUrls: ['./tagalongstory.component.css']
})
export class TagalongstoryComponent {
 // private baseUrl = inject(STORY_URL) + 'tagalongstories';
  private baseUrl = "/api/tagalongstories/"
  private http = inject(HttpClient);
  private service = inject(ImageServiceService)

  public tagalongstoriesAll: ITagalongStory[] = [];  // All stories
  public tagalongstoriesEnabled: ITagalongStory[] = [];
  public tagalongstoriesDisabled: ITagalongStory[] = [];

  public searchTerm: string = '';  // Search term from the input
  public filteredStories: ITagalongStory[] = [];  // Stories that match the search term

  constructor() {}

  ngOnInit(): void {
    const startTime = performance.now();
    const url = `${this.baseUrl}?v=${Math.random()}`;

    this.http.get<ITagalongStory[]>(url).subscribe(
      (stories) => {
        const loadTime = Math.round(performance.now() - startTime);
        console.log(`⚡ PERFORMANCE: ${stories.length} Geschichten in ${loadTime}ms geladen`);
        console.log(`🗂️ Erste Geschichte hat storyIcon:`, stories[0]?.storyIcon);

        this.tagalongstoriesAll = stories.map(story => ({
          ...story,
          enabled: !!story.enabled
        }));
        this.filteredStories = this.tagalongstoriesAll;
      },
      (error) => {
        console.error("Fehler beim Laden der Geschichten:", error);
      }
    );
  }
  

  // Function to filter the stories based on the search term
  filterStories(): void {
    if (this.searchTerm.trim() === '') {
      // If search term is empty, show all stories
      this.filteredStories = this.tagalongstoriesAll;
    } else {
      // Filter stories based on the search term (case-insensitive)
      this.filteredStories = this.tagalongstoriesAll.filter(story =>
        story.name.toLowerCase().includes(this.searchTerm.toLowerCase())
      );
    }
  }

  public deleteStory(id: number): void {
    // Delete the story with the given ID
    this.http.delete(this.baseUrl + '/' + id).subscribe(() => {
      // Fetch the stories again to reflect the changes
      this.ngOnInit();
    });
  }

  // Function to get story details and ensure correct movement and duration are displayed
  public getStoryDetails(id: number): void {
    this.http.get<ITagalongStory>(`${this.baseUrl}/${id}`).subscribe(story => {
      const index = this.filteredStories.findIndex(s => s.id === id);
      if (index !== -1) {
        this.filteredStories[index] = { ...story };
      }
    });
  }

  public deleteStoryWithId(id: number, name: string){

    if(confirm("Sicher das Sie die Geschichte '" + name.valueOf() + "' gelöscht werden soll?")){
      this.service.deleteStory(id).subscribe({
        next: data => {console.log("story gelöscht" + id),
          window.location.reload()
        },
        error: error => //alert("fehler beim löschen" + error.message)
        {console.log("gelöscht")
        window.location.reload()}
      })
    }
  }

  changeEnable(id: number, currentValue: boolean){
    if(currentValue){
      this.service.enablingStory(id, false).subscribe({
        next: data => window.location.reload(),
        error: error => alert("fehler beim wechseln des anzeigezustandes " + error.message)
      })
    }
    else{
      this.service.enablingStory(id, false).subscribe({
        next: data => window.location.reload(),
        error: error => alert("fehler beim wechseln des anzeigezustandes " + error.message)
      })
    }
  }
  
  // IMAGESERVER: Verwende direkt den neuen Imageserver
  getImageSrc(icon: string): string {
    // Ignoriere Base64 komplett - wird über storyIcon.id geholt
    return 'assets/images/imageNotFound.png';
  }

  // Neue Methode für Imageserver basierend auf Story-Objekt
  getImageSrcFromStory(story: any): string {
    // Prüfe ob storyIcon.id vorhanden (neues Backend)
    if (story.storyIcon?.id) {
      const imageUrl = `https://vm107.htl-leonding.ac.at/api/image/picture/${story.storyIcon.id}`;
      console.log(`� IMAGESERVER: Loading image ${story.storyIcon.id} from server`);
      return imageUrl;
    }
    
    // Fallback: Standard-Bild
    return 'assets/images/imageNotFound.png';
  }

  // Fehlerbehandlung für Bilder
  onImageError(event: any) {
    console.log('Bild konnte nicht geladen werden, verwende Fallback');
    event.target.src = 'assets/images/imageNotFound.png';
  }
  
  // IMAGESERVER: Prüfe ob Story eine Image-ID hat
  hasValidIcon(icon: string): boolean {
    // Wird nicht mehr verwendet - siehe hasValidImageFromStory
    return false;
  }

  // Neue Methode für Imageserver-Validierung
  hasValidImageFromStory(story: any): boolean {
    return !!(story.storyIcon?.id);
  }
}