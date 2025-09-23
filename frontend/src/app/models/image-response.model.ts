import { ImageJson } from "./image-json.model";

export interface ImageResponse {
    total: number;
    items: ImageJson[];
}
