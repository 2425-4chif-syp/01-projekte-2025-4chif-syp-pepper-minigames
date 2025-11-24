import { Routes } from '@angular/router';

import { Week } from './components/week/week';
import { Orders } from './components/orders/orders';
import { Residents } from './components/residents/residents';
import { Dishes } from './components/dishes/dishes';
import { Planner } from './components/planner/planner';

export const routes: Routes = [
  { path: 'week', component: Week },
  { path: 'orders', component: Orders },
  { path: 'residents', component: Residents },
  { path: 'dishes', component: Dishes },
  { path: 'planner', component: Planner },
  { path: '', redirectTo: '/week', pathMatch: 'full' }
];
