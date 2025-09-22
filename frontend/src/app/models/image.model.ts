import { Person } from "./person.model";

export interface ImageModel {
    description: string;
    person: number | null;
    base64Image: string;
}
