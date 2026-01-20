import { ApiFoodRef } from './api-food-ref.model';

export interface ApiMealPlanUpdate {
  weekNumber: number;
  weekDay: number;
  soup?: ApiFoodRef | null;
  lunch1?: ApiFoodRef | null;
  lunch2?: ApiFoodRef | null;
  lunchDessert?: ApiFoodRef | null;
  dinner1?: ApiFoodRef | null;
  dinner2?: ApiFoodRef | null;
}
