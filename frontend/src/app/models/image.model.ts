import { Person } from "./person.model";

export interface Image {
    description: string;
    id: number;
    person: Person;
}
