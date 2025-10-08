import { Person } from "./person.model";

export interface ImageJson {
    href: string;
    id: number;
    description: string;
    person: Person | null;
}
