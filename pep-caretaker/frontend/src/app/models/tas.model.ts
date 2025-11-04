import { ImagePreview } from "./image-preview.model";
import { ImageDto } from "./imageDto.model";
import { Move } from "./move.model";

export interface Tas {
  text: string;
  move: Move;
  durationInSeconds: number;
  image: ImagePreview;
  id: number;
  imageBase64: string;
}
