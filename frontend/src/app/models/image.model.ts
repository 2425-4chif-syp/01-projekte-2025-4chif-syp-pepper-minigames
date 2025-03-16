import { Person } from "./person.model";

export interface ImageModel {
    description: string;
    person: Person | null;
    base64Image: string;
}
