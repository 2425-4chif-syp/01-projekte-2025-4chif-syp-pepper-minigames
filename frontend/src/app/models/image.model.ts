import { Person } from "./person.model";

export interface ImageModel {
    description: string;
    personId: number | null;
    base64Image: string;
}
