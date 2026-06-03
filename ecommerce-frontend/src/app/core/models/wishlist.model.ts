export interface WishlistItem {
  productId: string;
  addedAt: string;
  notifyOnRestock: boolean;
}

export interface Wishlist {
  userId: string;
  items: WishlistItem[];
}

export interface AddToWishlistRequest {
  productId: string;
  notifyOnRestock: boolean;
}