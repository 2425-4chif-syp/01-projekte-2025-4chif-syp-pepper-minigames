import { Routes } from '@angular/router';
import { TagalongstoryComponent } from './tagalongstory/tagalongstory.component';
import { HomePageComponent } from './home-page/home-page.component';
import { MemoryComponent } from './memory/memory.component';
import { CreatestoryComponent } from './createstory/createstory.component';
import { EditstoryComponent } from './editstory/editstory.component';

export const routes: Routes = [
  {path: 'tagalongstory', component: TagalongstoryComponent},
  {path: '', component: HomePageComponent},
  {path: 'memory', component: MemoryComponent},
  {path: 'createstory', component: CreatestoryComponent},
  {path: 'editstory/:id', component: EditstoryComponent}
];
