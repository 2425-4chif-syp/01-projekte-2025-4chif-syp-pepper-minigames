import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { FoodManagementComponent } from './food-management/food-management.component';
import { WeekPlanManagementComponent } from './week-plan-management/week-plan-management.component';
import { FoodEditorComponent } from './food-editor/food-editor.component';
import { ManageUsersComponent } from './manage-users/manage-users.component';
import { OverviewOrdersComponent } from './overview-orders/overview-orders.component';
import {SelectUserComponent} from './select-user/select-user.component';
import {SelectWeekdayComponent} from './select-weekday/select-weekday.component';
import { SelectMenuComponent } from './select-menu/select-menu.component';
import { RasterComponent } from './raster/raster.component';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'food-management'},
  { path: 'food-management', component: FoodManagementComponent},
  { path: 'week-plan-management', component: WeekPlanManagementComponent},
  { path: 'food-editor', component: FoodEditorComponent},
  { path: 'manage-users', component: ManageUsersComponent},
  { path: 'overview-orders', component: OverviewOrdersComponent},
  { path: 'create-order', component: SelectUserComponent},
  {path: 'select-weekday/:name', component: SelectWeekdayComponent},
  {path: 'select-menu/:name/:date', component: SelectMenuComponent},
  {path: 'raster', component: RasterComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
