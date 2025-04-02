import { Component, inject } from '@angular/core';
import { RouterOutlet, RouterModule } from '@angular/router';
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
  imports: [RouterOutlet, RouterModule, CommonModule, FormsModule],
  templateUrl: './tagalongstory.component.html',
  styleUrls: ['./tagalongstory.component.css']
})
export class TagalongstoryComponent {
 // private baseUrl = inject(STORY_URL) + 'tagalongstories';
  private baseUrl = "http://vm88.htl-leonding.ac.at:8080/api/tagalongstories"
  private http = inject(HttpClient);
  private service = inject(ImageServiceService)

  public tagalongstoriesAll: ITagalongStory[] = [];  // All stories
  public tagalongstoriesEnabled: ITagalongStory[] = [];
  public tagalongstoriesDisabled: ITagalongStory[] = [];

  public searchTerm: string = '';  // Search term from the input
  public filteredStories: ITagalongStory[] = [];  // Stories that match the search term

  constructor() {}

  ngOnInit(): void {
    this.http.get<ITagalongStory[]>(this.baseUrl).subscribe(
      (stories) => {
        // Setze `enabled` als Boolean-Wert
        console.log(stories)
        this.tagalongstoriesAll = stories.map(story => ({
          ...story,
          enabled: !!story.enabled // Wandelt `undefined` oder `null` in `false` um
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
}