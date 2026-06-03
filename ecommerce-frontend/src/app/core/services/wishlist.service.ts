import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Wishlist, AddToWishlistRequest } from '../models/wishlist.model';

@Injectable({ providedIn: 'root' })
export class WishlistService {
  
  // State management to keep the UI reactive
  private wishlistSubject = new BehaviorSubject<Wishlist | null>(null);
  public wishlist$ = this.wishlistSubject.asObservable();

  constructor(private http: HttpClient) {}

  // 1. Fetch the user's wishlist
  getWishlist(): Observable<Wishlist> {
    return this.http.get<Wishlist>(`${environment.services.wishlists}/me`).pipe(
      tap(wishlist => this.wishlistSubject.next(wishlist))
    );
  }

  // 2. Add an item and automatically refresh state
  addProduct(request: AddToWishlistRequest): Observable<void> {
    return this.http.post<void>(`${environment.services.wishlists}/items`, request).pipe(
      tap(() => this.getWishlist().subscribe())
    );
  }

  // 3. Remove an item and automatically refresh state
  removeProduct(productId: string): Observable<void> {
    return this.http.delete<void>(`${environment.services.wishlists}/items/${productId}`).pipe(
      tap(() => this.getWishlist().subscribe())
    );
  }

  updateNotifyStatus(productId: string, notify: boolean): Observable<void> {
    return this.http.patch<void>(`${environment.services.wishlists}/items/${productId}/notify?notify=${notify}`, {}).pipe(
      tap(() => this.getWishlist().subscribe()) // On rafraîchit la liste
    );
  }

  // Helper method for components to check if a product is wishlisted
  isInWishlist(productId: string): boolean {
    const currentList = this.wishlistSubject.value;
    return currentList?.items.some(item => item.productId === productId) ?? false;
  }
}