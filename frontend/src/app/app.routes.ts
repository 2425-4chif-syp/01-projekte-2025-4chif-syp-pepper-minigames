import { Routes } from '@angular/router';
import { TagalongstoryComponent } from './tagalongstory/tagalongstory.component';
import { HomePageComponent } from './home-page/home-page.component';
import { AddStepComponent } from './add-step/add-step.component';
import { EditStoryComponent } from './edit-story/edit-story.component';
import { PersonEntryComponent } from './person-entry/person-entry.component';
import { CreateStoryComponent } from './create-story/create-story.component';
import { PictureOverviewComponent } from './picture-overview/picture-overview.component';
import { ResidentsComponent } from './residents/residents.component';
import { ImageUploadComponent } from './image-upload/image-upload.component';

export const routes: Routes = [
    { path: 'tagalongstory', component: TagalongstoryComponent },
    { path: '', component: HomePageComponent },
    { path: 'addstep/:id', component: AddStepComponent },
    { path: 'editstory/:id', component: EditStoryComponent },
    { path: 'person-entry', component: PersonEntryComponent },
    { path: 'createstory', component: CreateStoryComponent },
    { path: 'createstory/:id', component: CreateStoryComponent },
    { path: 'pictures', component: PictureOverviewComponent },
    { path: 'imageUpload', component: ImageUploadComponent },
    { path: 'residents', component: ResidentsComponent }
];
