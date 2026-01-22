import { ApiFood } from './api-food.model';

export interface ApiMealPlan {
  id?: number;
  weekNumber: number;
  weekDay: number;
  soup?: ApiFood | null;
  lunch1?: ApiFood | null;
  lunch2?: ApiFood | null;
  lunchDessert?: ApiFood | null;
  dinner1?: ApiFood | null;
  dinner2?: ApiFood | null;
}
