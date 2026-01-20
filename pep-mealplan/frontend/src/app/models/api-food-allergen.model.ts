import { Allergen } from './allergen.model';

export interface ApiFoodAllergen {
  id?: { foodId: number; allergenShortname: string };
  allergen?: Allergen;
}
