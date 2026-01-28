// Legacy Picture interface (for Food references)
export interface Picture {
  id?: number;
  description?: string | null;
  url?: string | null;
  // Legacy fields for food-management compatibility
  base64?: string;
  name?: string;
  mediaType?: string;
}

// For uploading images
export interface ImageUpload {
  base64Image: string;
  description?: string;
  imageUrl?: string;
}

// Response from API
export interface ImageDto {
  id: number;
  base64Image: string | null;
  imageUrl: string | null;
  description: string | null;
}

// List response with href
export interface ImageJson {
  id: number;
  href: string;
  description: string | null;
}
