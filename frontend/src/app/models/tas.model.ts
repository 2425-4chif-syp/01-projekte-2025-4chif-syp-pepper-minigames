import { ImagePreview } from "./image-preview.model";
import { ImageDto } from "./imageDto.model";
import { Move } from "./move.model";

export interface Tas {
  text: string;
  move: Move;
  duration: number;
  image: ImagePreview;
  id: number;
}
