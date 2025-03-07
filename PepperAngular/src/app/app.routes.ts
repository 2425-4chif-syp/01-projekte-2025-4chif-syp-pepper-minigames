import { Routes } from '@angular/router';
import { AddMoveComponent } from './add-move/add-move.component';
import { StorySelectionComponent } from './story-selection/story-selection.component';
import { NewStoryComponent } from './new-story/new-story.component';

export const routes: Routes = [
  {path: '', redirectTo: 'story-selection', pathMatch: 'full'},
  {path: 'story-selection', component: StorySelectionComponent},
  {path: 'new-story/:id', component: NewStoryComponent},
  {path: 'new-story', component: NewStoryComponent},
  {path: 'add-move', component: AddMoveComponent}
];
