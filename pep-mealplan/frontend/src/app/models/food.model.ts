import { Picture } from './picture.model';

export interface Food {
  id?: number;
  name: string;
  type: string;
  allergens: string[];
  picture?: Picture | null;
}
