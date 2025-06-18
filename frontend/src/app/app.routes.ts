import { Routes } from '@angular/router';
import { TagalongstoryComponent } from './tagalongstory/tagalongstory.component';
import { HomePageComponent } from './home-page/home-page.component';
import { EditstoryComponent } from './editstory/editstory.component';
import { AddstepComponent } from './addstep/addstep.component';
import { PersonEntryComponent } from './person-entry/person-entry.component';
import { CreatestoryComponent } from './createstory/createstory.component';
import { PictureOverviewComponent } from './picture-overview/picture-overview.component';
import { ImageuploadComponent } from './imageupload/imageupload.component';
import { ResidentsComponent } from './residents/residents.component';
import { ResidentDetailsComponent } from './resident-details/resident-details.component';

export const routes: Routes = [
  {path: 'tagalongstory', component: TagalongstoryComponent},
  {path: '', component: HomePageComponent},
  {path: 'addstep/:id', component: AddstepComponent},
  {path: 'editstory/:id', component: EditstoryComponent},
  {path: 'person-entry', component: PersonEntryComponent},
  { path: 'createstory', component: CreatestoryComponent },
  { path: 'createstory/:id', component: CreatestoryComponent },
  { path: 'pictures', component: PictureOverviewComponent },
  {path: 'imageUpload', component: ImageuploadComponent},
  {path: 'residents', component: ResidentsComponent},
  {path: 'residentDetails/:id', component: ResidentDetailsComponent}
];

