import { Person } from "./person.model";

export interface ImageDto {
        id: number;
        description: string;
        person: Person | null;
        base64Image: string;
}