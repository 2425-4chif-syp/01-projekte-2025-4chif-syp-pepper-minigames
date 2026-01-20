import { Picture } from './picture.model';
import { ApiFoodAllergen } from './api-food-allergen.model';

export interface ApiFood {
  id?: number;
  name: string;
  type: string;
  allergens?: ApiFoodAllergen[];
  picture?: Picture | null;
}
