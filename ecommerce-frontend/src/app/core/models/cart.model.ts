export interface CartItem {
  productId: string;
  quantity: number;
  // Champs facultatifs pour l'affichage (seront remplis après récupération)
  productName?: string;
  unitPrice?: number;
  imageUrl?: string;
}

export interface Cart {
  id: string;
  userId: string;
  items: CartItem[];
  createdAt: string;
  updatedAt: string;
}