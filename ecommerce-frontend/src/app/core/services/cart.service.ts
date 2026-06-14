import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, forkJoin, of, BehaviorSubject } from 'rxjs';
import { switchMap, map, tap } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { Cart, CartItem } from '../models/cart.model';
import { Product } from '../models/product.model';

@Injectable({ providedIn: 'root' })
export class CartService {

  // 1. FIXED: Now pulls dynamically from the Gateway URL in environment.ts!
  private baseUrl = `${environment.services.cart}/my-cart`;

  // Global Cart State for UI Badges
  private cartUpdatedSubject = new BehaviorSubject<void>(undefined);
  cartUpdated$ = this.cartUpdatedSubject.asObservable();

  constructor(private http: HttpClient) {}

  getCart(): Observable<Cart> {
    return this.http.get<Cart>(this.baseUrl);
  }

  addItem(productId: string, quantity: number = 1): Observable<Cart> {
    return this.http.post<Cart>(`${this.baseUrl}/items`, { productId, quantity }).pipe(
      tap(() => this.cartUpdatedSubject.next())
    );
  }

  removeItem(productId: string): Observable<Cart> {
    return this.http.delete<Cart>(`${this.baseUrl}/items/${productId}`).pipe(
      tap(() => this.cartUpdatedSubject.next())
    );
  }

  clearCart(): Observable<void> {
    return this.http.delete<void>(this.baseUrl).pipe(
      tap(() => this.cartUpdatedSubject.next())
    );
  }

  // Récupère le panier enrichi avec les détails des produits
  getEnrichedCart(): Observable<{ cart: Cart; itemsDetailed: CartItem[] }> {
    return this.getCart().pipe(
      switchMap(cart => {
        if (cart.items.length === 0) {
          return of({ cart, itemsDetailed: [] });
        }
        const productRequests = cart.items.map(item =>
          // This one was already correct!
          this.http.get<Product>(`${environment.services.products}/${item.productId}`)
        );
        return forkJoin(productRequests).pipe(
          map(products => {
            const itemsDetailed: CartItem[] = cart.items.map((item, index) => {
              
              // --- CHANGED THIS SECTION ---
              // Safely grab the primary image, or the first image, or undefined
              const mainImage = products[index]?.images?.find(img => img.isPrimary) || products[index]?.images?.[0];
              
              return {
                ...item,
                productName: products[index]?.name || 'Inconnu',
                unitPrice: products[index]?.price || 0,
                // Pass the gateway URL + filename to the cart item
                imageUrl: mainImage ? `${environment.apiGatewayUrl}/api/v1/products/images/${mainImage.imageUrl}` : undefined
              };
            });
            return { cart, itemsDetailed };
          })
        );
      })
    );
  }

  updateItemQuantity(productId: string, quantity: number): Observable<Cart> {
    return this.http.put<Cart>(`${this.baseUrl}/items/${productId}`, { quantity }).pipe(
      tap(() => this.cartUpdatedSubject.next())
    );
  }
}