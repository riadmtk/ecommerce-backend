export interface Product {
  id: string;
  name: string;
  description: string;
  price: number;
  stockQuantity: number;
  category?: string;
  imageUrl?: string;
  createdAt: string;
}

export interface PagedResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
}