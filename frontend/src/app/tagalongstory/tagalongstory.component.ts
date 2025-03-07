import { Component, inject } from '@angular/core';
import { RouterOutlet, RouterModule } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { STORY_URL } from '../app.config';
import { ITagalongStory } from '../../models/tagalongstories.model';
import { CommonModule } from '@angular/common';
import { forkJoin } from 'rxjs';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-tagalongstory',
  standalone: true,
  imports: [RouterOutlet, RouterModule, CommonModule, FormsModule],
  templateUrl: './tagalongstory.component.html',
  styleUrls: ['./tagalongstory.component.css']
})
export class TagalongstoryComponent {
  private baseUrl = inject(STORY_URL);
  private http = inject(HttpClient);
  
  public tagalongstoriesAll: ITagalongStory[] = [];  // All stories
  public tagalongstoriesEnabled: ITagalongStory[] = [];
  public tagalongstoriesDisabled: ITagalongStory[] = [];

  public searchTerm: string = '';  // Search term from the input
  public filteredStories: ITagalongStory[] = [];  // Stories that match the search term

  constructor() {}

  ngOnInit(): void {
    // Fetch both enabled and disabled stories
    forkJoin([
      this.http.get<ITagalongStory[]>(this.baseUrl + "?withoutDisabled=false"),
      this.http.get<ITagalongStory[]>(this.baseUrl + "?withoutDisabled=true")
    ]).subscribe(([disabledStories, enabledStories]) => {
      // Assign fetched data to the corresponding arrays
      this.tagalongstoriesDisabled = disabledStories.map(story => ({
        ...story,
        isEnabled: 'false'
      }));
  
      this.tagalongstoriesEnabled = enabledStories.map(story => ({
        ...story,
        isEnabled: 'true'
      }));
  
      // Use a map to ensure no duplicates
      const storyMap = new Map<number, ITagalongStory>();

      // Add both disabled and enabled stories to the map
      this.tagalongstoriesDisabled.forEach(story => storyMap.set(story.id, story));
      this.tagalongstoriesEnabled.forEach(story => storyMap.set(story.id, story));

      // Convert the map back to an array
      this.tagalongstoriesAll = Array.from(storyMap.values());
  
      // Initially, display all stories
      this.filteredStories = this.tagalongstoriesAll;
    });
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
}
