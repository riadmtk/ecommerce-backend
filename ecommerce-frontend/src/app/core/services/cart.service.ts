import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, forkJoin, of } from 'rxjs';
import { switchMap, map } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { Cart, CartItem } from '../models/cart.model';
import { Product } from '../models/product.model';

@Injectable({ providedIn: 'root' })
export class CartService {

  private baseUrl = 'http://localhost:8084/api/v1/carts/my-cart';

  constructor(private http: HttpClient) {}

  getCart(): Observable<Cart> {
    return this.http.get<Cart>(this.baseUrl);
  }

  addItem(productId: string, quantity: number = 1): Observable<Cart> {
    return this.http.post<Cart>(`${this.baseUrl}/items`, { productId, quantity });
  }

  removeItem(productId: string): Observable<Cart> {
    return this.http.delete<Cart>(`${this.baseUrl}/items/${productId}`);
  }

  clearCart(): Observable<void> {
    return this.http.delete<void>(this.baseUrl);
  }

  // Récupère le panier enrichi avec les détails des produits
  getEnrichedCart(): Observable<{ cart: Cart; itemsDetailed: CartItem[] }> {
    return this.getCart().pipe(
      switchMap(cart => {
        if (cart.items.length === 0) {
          return of({ cart, itemsDetailed: [] });
        }
        const productRequests = cart.items.map(item =>
          this.http.get<Product>(`${environment.services.products}/${item.productId}`)
        );
        return forkJoin(productRequests).pipe(
          map(products => {
            const itemsDetailed: CartItem[] = cart.items.map((item, index) => ({
              ...item,
              productName: products[index]?.name || 'Inconnu',
              unitPrice: products[index]?.price || 0,
              imageUrl: products[index]?.imageUrl
            }));
            return { cart, itemsDetailed };
          })
        );
      })
    );
  }

  updateItemQuantity(productId: string, quantity: number): Observable<Cart> {
    return this.http.put<Cart>(`${this.baseUrl}/items/${productId}`, { quantity });
}
}