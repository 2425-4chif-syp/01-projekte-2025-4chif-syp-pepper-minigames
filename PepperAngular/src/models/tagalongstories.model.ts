export interface ITagalongStory {
  id: number;
  name: string;
  icon: string;
  gameType: IGameType;
  enabled: boolean;
}

export interface IStep {
  id?: number;
  duration: number;
  moveNameAndDuration?: string | null;
  text: string;
  image: string;
  index: number;
  tagAlongStoryId?: number | null;
}

export interface IGameType{
  id: string;
  name: string;
}
