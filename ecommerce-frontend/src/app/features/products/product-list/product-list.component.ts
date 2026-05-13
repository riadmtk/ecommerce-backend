import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ProductService } from '../../../core/services/product.service';
import { Product } from '../../../core/models/product.model';
import { CurrencyMadPipe } from '../../../shared/pipes/currency-mad.pipe';
import { environment } from '../../../../environments/environment';

// ← 1. IMPORT YOUR SHARED SEARCH BAR
import { SearchBarComponent } from '../../../shared/components/search-bar/search-bar.component'; 

@Component({
  selector: 'app-product-list',
  standalone: true,
  // ← 2. ADD IT TO THE IMPORTS ARRAY
  imports: [CommonModule, RouterModule, CurrencyMadPipe, SearchBarComponent], 
  templateUrl: './product-list.component.html',
  styleUrls: ['./product-list.component.scss']
})
export class ProductListComponent implements OnInit {

  products: Product[] = [];
  isLoading = true;
  errorMessage = '';

  constructor(
    private productService: ProductService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadAllProducts();
  }

  loadAllProducts(): void {
    this.isLoading = true;
    this.productService.getAll().subscribe({
      next: (products) => {
        this.products = products;
        this.isLoading = false;
        this.cdr.detectChanges(); 
      },
      error: () => {
        this.errorMessage = 'Erreur lors du chargement des produits';
        this.isLoading = false;
      }
    });
  }

  getProductImage(product: Product): string {
    if (product.images && product.images.length > 0) {
      const mainImage = product.images.find(img => img.isPrimary) || product.images[0];
      return `${environment.apiGatewayUrl}/api/v1/products/images/${mainImage.imageUrl}`;
    }
    
    if (product.imageUrls && product.imageUrls.length > 0) {
      return `${environment.apiGatewayUrl}/api/v1/products/images/${product.imageUrls[0]}`;
    }

    return 'assets/placeholder.png'; 
  }
}