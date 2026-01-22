import { DayPlan } from './day-plan.model';

export interface WeekPlan {
  dayPlans: DayPlan[];
  cyclePosition: number;
}
