// Optimiertes Interface nur für die Übersicht
export interface ITagalongStorySummary {
  id: number;
  name: string;
  enabled: boolean;
  storyIcon?: { 
    id: number;
    description?: string;
  };
}