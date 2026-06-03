import { Component, OnInit, ChangeDetectorRef, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { ProductService } from '../../../core/services/product.service';
import { Product } from '../../../core/models/product.model';
import { AuthService } from '../../../core/auth/auth.service';
import { CartService } from '../../../core/services/cart.service';
import { WishlistService } from '../../../core/services/wishlist.service'; // 👈 IMPORT
import { CurrencyMadPipe } from '../../../shared/pipes/currency-mad.pipe';
import { Observable, Subscription, map } from 'rxjs';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-product-detail',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, CurrencyMadPipe],
  templateUrl: './product-detail.component.html',
  styleUrls: ['./product-detail.component.scss']
})
export class ProductDetailComponent implements OnInit, OnDestroy {

  product: Product | null = null;
  isLoading = true;
  errorMessage = '';
  isAdmin$: Observable<boolean>;
  isDeleting = false;

  // Gestion du panier
  quantity: number = 1;
  showAddToCartModal: boolean = false;
  addingToCart: boolean = false;
  cartSuccessMessage: string = '';

  // 🎯 Gestion de la Wishlist
  isWishlisted: boolean = false;
  isProcessingWishlist: boolean = false;
  private wishlistSub!: Subscription;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private productService: ProductService,
    private cartService: CartService,
    private wishlistService: WishlistService, // 👈 INJECTED
    private cdr: ChangeDetectorRef,
    public authService: AuthService
  ) {
    this.isAdmin$ = this.authService.currentUser$.pipe(
      map(user => user?.role === 'ADMIN')
    );
  }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (!id) {
      this.router.navigate(['/products']);
      return;
    }

    this.productService.getById(id).subscribe({
      next: (product) => {
        this.product = product;
        this.isLoading = false;
        
        // 🎯 On s'abonne aux changements de la wishlist pour mettre à jour l'icône
        if (this.authService.isLoggedIn()) {
          // Charge le panier initial
          this.wishlistService.getWishlist().subscribe();
          
          this.wishlistSub = this.wishlistService.wishlist$.subscribe(() => {
            this.isWishlisted = this.wishlistService.isInWishlist(product.id);
            this.cdr.detectChanges();
          });
        }
        
        this.cdr.detectChanges();
      },
      error: () => {
        this.errorMessage = 'Produit introuvable';
        this.isLoading = false;
        this.cdr.detectChanges();
      }
    });
  }

  ngOnDestroy(): void {
    if (this.wishlistSub) {
      this.wishlistSub.unsubscribe();
    }
  }

  // --- 🎯 Nouvelle Logique Wishlist ---
  toggleWishlist(): void {
    if (!this.product) return;
    
    if (!this.authService.isLoggedIn()) {
      this.router.navigate(['/auth/login']);
      return;
    }

    this.isProcessingWishlist = true;

    if (this.isWishlisted) {
      this.wishlistService.removeProduct(this.product.id).subscribe({
        next: () => {
          this.isProcessingWishlist = false;
          this.cdr.detectChanges();
        },
        error: () => {
          this.isProcessingWishlist = false;
        }
      });
    } else {
      // Si le stock est à 0, on active la notification de restockage !
      const notify = this.product.stockQuantity === 0;
      
      this.wishlistService.addProduct({ productId: this.product.id, notifyOnRestock: notify }).subscribe({
        next: () => {
          this.isProcessingWishlist = false;
          this.cdr.detectChanges();
        },
        error: () => {
          this.isProcessingWishlist = false;
        }
      });
    }
  }

  // ... (keep all your existing Cart, Delete, and getProductImage methods here) ...
  
  openAddToCartModal(): void {
    if (!this.product) return;
    this.quantity = 1;
    this.showAddToCartModal = true;
  }

  closeAddToCartModal(): void {
    this.showAddToCartModal = false;
  }

  confirmAddToCart(): void {
    if (!this.product) return;
    this.addingToCart = true;
    this.cartService.addItem(this.product.id, this.quantity).subscribe({
      next: () => {
        this.cartSuccessMessage = `${this.quantity} x ${this.product!.name} ajouté(s) au panier ✅`;
        this.showAddToCartModal = false;
        this.addingToCart = false;
        setTimeout(() => {
          this.cartSuccessMessage = '';
          this.cdr.detectChanges();
        }, 3000);
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Erreur ajout panier', err);
        this.errorMessage = 'Erreur lors de l\'ajout au panier';
        this.addingToCart = false;
        this.showAddToCartModal = false;
        this.cdr.detectChanges();
      }
    });
  }

  incrementQuantity(): void {
    if (this.product && this.quantity < this.product.stockQuantity) {
      this.quantity++;
    }
  }

  decrementQuantity(): void {
    if (this.quantity > 1) {
      this.quantity--;
    }
  }

  deleteProduct(): void {
    if (!this.product) return;
    const confirmDelete = confirm(
      `Êtes-vous sûr de vouloir supprimer le produit "${this.product.name}" ?`
    );
    if (!confirmDelete) return;

    this.isDeleting = true;
    this.productService.delete(this.product.id).subscribe({
      next: () => {
        this.router.navigate(['/admin/dashboard/products']);
      },
      error: () => {
        this.errorMessage = 'Erreur lors de la suppression du produit';
        this.isDeleting = false;
        this.cdr.detectChanges();
      }
    });
  }

  getProductImage(product: Product): string {
    if (product.images && product.images.length > 0) {
      const mainImage = product.images.find(img => img.isPrimary) || product.images[0];
      return `${environment.apiGatewayUrl}/api/v1/products/images/${mainImage.imageUrl}`;
    }
    return 'assets/placeholder.png'; 
  }
}