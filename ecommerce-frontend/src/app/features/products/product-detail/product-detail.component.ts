import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { ProductService } from '../../../core/services/product.service';
import { Product } from '../../../core/models/product.model';
import { AuthService } from '../../../core/auth/auth.service';
import { CartService } from '../../../core/services/cart.service';
import { CurrencyMadPipe } from '../../../shared/pipes/currency-mad.pipe';
import { Observable, map } from 'rxjs';
import { environment } from '../../../../environments/environment'; // ← ADDED IMPORT

@Component({
  selector: 'app-product-detail',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, CurrencyMadPipe],
  templateUrl: './product-detail.component.html',
  styleUrls: ['./product-detail.component.scss']
})
export class ProductDetailComponent implements OnInit {

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

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private productService: ProductService,
    private cartService: CartService,
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
        this.cdr.detectChanges();
      },
      error: () => {
        this.errorMessage = 'Produit introuvable';
        this.isLoading = false;
        this.cdr.detectChanges();
      }
    });
  }

  // --- Panier ---
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

  // --- Suppression produit (admin) ---
  deleteProduct(): void {
    if (!this.product) return;
    const confirmDelete = confirm(
      `Êtes-vous sûr de vouloir supprimer le produit "${this.product.name}" ?`
    );
    if (!confirmDelete) return;

    this.isDeleting = true;
    this.productService.delete(this.product.id).subscribe({
      next: () => {
        this.router.navigate(['/products']);
      },
      error: () => {
        this.errorMessage = 'Erreur lors de la suppression du produit';
        this.isDeleting = false;
        this.cdr.detectChanges();
      }
    });
  }

  // --- NOUVELLE MÉTHODE D'AIDE POUR L'AFFICHAGE DES IMAGES ---
  getProductImage(product: Product): string {
    if (product.images && product.images.length > 0) {
      const mainImage = product.images.find(img => img.isPrimary) || product.images[0];
      return `${environment.apiGatewayUrl}/api/v1/products/images/${mainImage.imageUrl}`;
    }
    return 'assets/placeholder.png'; 
  }
}