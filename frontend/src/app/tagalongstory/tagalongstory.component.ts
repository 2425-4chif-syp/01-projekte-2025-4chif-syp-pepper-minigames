import { CommonModule } from '@angular/common';
import { Component, inject, OnInit, signal } from '@angular/core';
import { RouterOutlet, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { TagalongstoryService } from '../services/tagalongstory.service';
import { ITagalongStory } from '../models/tagalongstory.model';

@Component({
  selector: 'app-tagalongstory',
  imports: [RouterOutlet, RouterModule, CommonModule, FormsModule],
  templateUrl: './tagalongstory.component.html',
  styleUrl: './tagalongstory.component.css'
})
export class TagalongstoryComponent implements OnInit{
  private tasService = inject(TagalongstoryService);
  public tagalongstories = signal<ITagalongStory[]>([]);
  public searchTerm = signal<string>("");

  constructor() {}

  ngOnInit(): void {
    this.tasService.getAllTagalongstories().subscribe({
      next: data => {
        this.tagalongstories.set(data)
      }
    })
  }


  public deleteStoryWithId(id: number, name: string){
    if(confirm("Sicher das Sie die Geschichte '" + name.valueOf() + "' gelöscht werden soll?")){
      this.tasService.deleteStory(id).subscribe({
        next: data => {
          console.log("story gelöscht" + id);
          location.reload();
        }
      })
    }
  }

  changeEnable(id: number, currentValue: boolean){
    if(currentValue){
      this.tasService.enablingStory(id, false).subscribe({
        next: data => window.location.reload(),
        error: error => alert("fehler beim wechseln des anzeigezustandes " + error.message)
      })
    }
    else{
      this.tasService.enablingStory(id, false).subscribe({
        next: data => window.location.reload(),
        error: error => alert("fehler beim wechseln des anzeigezustandes " + error.message)
      })
    }
  }
}
