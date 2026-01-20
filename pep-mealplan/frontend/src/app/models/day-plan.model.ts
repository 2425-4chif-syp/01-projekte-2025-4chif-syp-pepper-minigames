export interface DayPlan {
  date: Date;
  daySoup: string;
  menuOne: string;
  menuTwo: string;
  dessert: string;
  eveningOne: string;
  eveningTwo: string;

  selectedMenu?: 'one' | 'two' | null;
  selectedEvening?: 'one' | 'two' | null;
}
