import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { WishlistService } from '../../core/services/wishlist.service';
import { ProductService } from '../../core/services/product.service';
import { Observable, combineLatest } from 'rxjs';
import { map, shareReplay, startWith } from 'rxjs/operators';
import { Wishlist, WishlistItem } from '../../core/models/wishlist.model';
import { Product } from '../../core/models/product.model';
import { environment } from '../../../environments/environment';

export interface WishlistItemWithProduct extends WishlistItem {
  product?: Product;
}

@Component({
  selector: 'app-wishlist',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './wishlist.component.html',
  styleUrls: ['./wishlist.component.scss']
})
export class WishlistComponent implements OnInit {
  
  wishlistWithProducts$: Observable<{ items: WishlistItemWithProduct[] } | null>;

  constructor(
    private wishlistService: WishlistService,
    private productService: ProductService
  ) {
    const products$ = this.productService.getAll().pipe(shareReplay(1));
    this.wishlistWithProducts$ = combineLatest([
      this.wishlistService.wishlist$,
      products$.pipe(startWith([]))
    ]).pipe(
      map(([wishlist, products]) => {
        if (!wishlist) return null;
        return {
          ...wishlist,
          items: wishlist.items.map(item => ({
            ...item,
            product: products.find((p: Product) => p.id === item.productId)
          }))
        };
      })
    );
  }

  ngOnInit(): void {
    this.wishlistService.getWishlist().subscribe();
  }

  removeItem(productId: string): void {
    this.wishlistService.removeProduct(productId).subscribe();
  }

  toggleNotify(productId: string, event: Event): void {
    const isChecked = (event.target as HTMLInputElement).checked;
    this.wishlistService.updateNotifyStatus(productId, isChecked).subscribe();
  }

  getProductImage(product: Product | undefined): string {
    if (product && product.images && product.images.length > 0) {
      const mainImage = product.images.find(img => img.isPrimary) || product.images[0];
      return `${environment.apiGatewayUrl}/api/v1/products/images/${mainImage.imageUrl}`;
    }
    return 'assets/placeholder.png'; 
  }
}