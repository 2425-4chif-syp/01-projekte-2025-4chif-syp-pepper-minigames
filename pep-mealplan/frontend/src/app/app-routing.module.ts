import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { FoodManagementComponent } from './food-management/food-management.component';
import { WeekPlanManagementComponent } from './week-plan-management/week-plan-management.component';
import { FoodEditorComponent } from './food-editor/food-editor.component';
import { ManageUsersComponent } from './manage-users/manage-users.component';
import { OverviewOrdersComponent } from './overview-orders/overview-orders.component';
import { SelectUserComponent } from './select-user/select-user.component';
import { SelectWeekdayComponent } from './select-weekday/select-weekday.component';
import { SelectMenuComponent } from './select-menu/select-menu.component';
import { RasterComponent } from './raster/raster.component';

export const routes: Routes = [
  { path: '', redirectTo: 'foods', pathMatch: 'full' },
  { path: 'foods', component: FoodManagementComponent },
  { path: 'menu-week', component: WeekPlanManagementComponent },
  { path: 'food', component: FoodEditorComponent },
  { path: 'users', component: ManageUsersComponent },
  { path: 'orders', component: OverviewOrdersComponent },
  { path: 'order', component: SelectUserComponent },
  { path: 'order-day/:name', component: SelectWeekdayComponent },
  { path: 'order-menu/:name/:date', component: SelectMenuComponent },
  { path: 'menu-grid', component: RasterComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
