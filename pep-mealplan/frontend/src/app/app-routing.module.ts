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
import { AuthGuard } from './guards/auth.guard';

export const routes: Routes = [
  { path: '', redirectTo: 'foods', pathMatch: 'full' },
  { path: 'foods', component: FoodManagementComponent, canActivate: [AuthGuard] },
  { path: 'menu-week', component: WeekPlanManagementComponent, canActivate: [AuthGuard] },
  { path: 'food', component: FoodEditorComponent, canActivate: [AuthGuard] },
  { path: 'users', component: ManageUsersComponent, canActivate: [AuthGuard] },
  { path: 'orders', component: OverviewOrdersComponent, canActivate: [AuthGuard] },
  { path: 'order', component: SelectUserComponent, canActivate: [AuthGuard] },
  { path: 'order-day/:name', component: SelectWeekdayComponent, canActivate: [AuthGuard] },
  { path: 'order-menu/:name/:date', component: SelectMenuComponent, canActivate: [AuthGuard] },
  { path: 'menu-grid', component: RasterComponent, canActivate: [AuthGuard] }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
