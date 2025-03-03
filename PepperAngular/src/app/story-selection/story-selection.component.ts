import { CommonModule } from '@angular/common';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { MatSlideToggleChange, MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatIconModule } from '@angular/material/icon';
import {MatRadioModule} from '@angular/material/radio';
import {MatDividerModule} from '@angular/material/divider';
import { Component } from '@angular/core';
import { Storys, StorysService } from '../storys.service';
import { Observable, debounceTime, distinctUntilChanged, filter, from, map, startWith, switchMap, tap } from 'rxjs';
@Component({
  selector: 'app-story-selection',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule, MatSlideToggleModule, MatIconModule, MatRadioModule, MatDividerModule,],
  templateUrl: './story-selection.component.html',
  styleUrls: ['./story-selection.component.scss']
})
export class StorySelectionComponent {

  /*
  tagAlongStories$ = ["Story 1", "Story 2", "Story 3", "Story 4", "Story 5", "Story 6", "Story 7", "Story 8"];

  deleteStory(index: number) {
    this.tagAlongStories$.splice(index, 1);
  }


  */

  constructor(public storyService: StorysService) { }

 //public gStorys$ = this.storyService.getTagalongstories();

 deleteStory(index: number) {
  this.storyService.deleteTagalongstory(index).subscribe(
    () => {
      this.input$ = this.getStorys();
    }
  );
}

getStorys(): Observable<Storys[]>{
  return this.formGroup.valueChanges.pipe(
    startWith({nameFilter: ''}),
    debounceTime(200),
    distinctUntilChanged(),
    tap(x => console.log(x)),
    switchMap(group => this.storyService.getTagalongstories().pipe(map(x => {
     return x.filter(y => y.name.toLowerCase().includes(group.nameFilter!.toLowerCase()))
    })))
    );;
}

 public formGroup = new FormGroup({
  nameFilter: new FormControl(''),
})

  // filter methode
 public input$ = this.getStorys();



  toggle(event: MatSlideToggleChange, story: Storys){
    console.log("toggle");
    const checked = event.checked;
    story.isEnabled = checked;
    this.storyService.putTagalongstory(story).subscribe(
      () => this.input$ = this.getStorys()
    );
  }
}
