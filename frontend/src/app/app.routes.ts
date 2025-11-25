import { Routes } from '@angular/router';
import { AuthGuard } from './auth.guard';
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
import { AddResidentComponent } from './add-resident/add-resident.component';
import { PreviewScreenComponent } from './preview-screen/preview-screen.component';

export const routes: Routes = [
  // Öffentliche Seiten - alle können zugreifen
  {path: '', component: HomePageComponent, canActivate: [AuthGuard]},
  {path: 'tagalongstory', component: TagalongstoryComponent, canActivate: [AuthGuard]},
  {path: 'previewScreen/:id', component: PreviewScreenComponent, canActivate: [AuthGuard]},
  
  // Story-Management - Admin & Caretaker
  {path: 'createstory', component: CreatestoryComponent, canActivate: [AuthGuard], data: { roles: ['admin', 'caretaker'] }},
  {path: 'createstory/:id', component: CreatestoryComponent, canActivate: [AuthGuard], data: { roles: ['admin', 'caretaker'] }},
  {path: 'editstory/:id', component: EditstoryComponent, canActivate: [AuthGuard], data: { roles: ['admin', 'caretaker'] }},
  {path: 'addstep/:id', component: AddstepComponent, canActivate: [AuthGuard], data: { roles: ['admin', 'caretaker'] }},
  
  // Bilder-Management - Admin & Caretaker
  {path: 'pictures', component: PictureOverviewComponent, canActivate: [AuthGuard], data: { roles: ['admin', 'caretaker'] }},
  {path: 'imageUpload', component: ImageuploadComponent, canActivate: [AuthGuard], data: { roles: ['admin', 'caretaker'] }},
  
  // Bewohner-Management - nur Admin
  {path: 'residents', component: ResidentsComponent, canActivate: [AuthGuard], data: { roles: ['admin'] }},
  {path: 'residentDetails/:id', component: ResidentDetailsComponent, canActivate: [AuthGuard], data: { roles: ['admin'] }},
  {path: 'residentAdd', component: AddResidentComponent, canActivate: [AuthGuard], data: { roles: ['admin'] }},
  {path: 'person-entry', component: PersonEntryComponent, canActivate: [AuthGuard], data: { roles: ['admin'] }}
];