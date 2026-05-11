import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ProductService } from '../../../core/services/product.service';
import { Product } from '../../../core/models/product.model';
import { CurrencyMadPipe } from '../../../shared/pipes/currency-mad.pipe';
import { environment } from '../../../../environments/environment'; 

@Component({
  selector: 'app-product-list',
  standalone: true,
  imports: [CommonModule, RouterModule, CurrencyMadPipe],
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
    this.productService.getAll().subscribe({
      next: (products) => {
        this.products = products;
        this.isLoading = false;
        this.cdr.detectChanges(); // ← force Angular à vérifier les changements
      },
      error: () => {
        this.errorMessage = 'Erreur lors du chargement des produits';
        this.isLoading = false;
      }
    });
  }

  // --- UPDATED HELPER METHOD ---
  getProductImage(product: Product): string {
    // 1. Check if the 'images' array exists and has at least one object
    if (product.images && product.images.length > 0) {
      
      // 2. Find the image marked as primary, or fallback to the first one in the list
      const mainImage = product.images.find(img => img.isPrimary) || product.images[0];
      
      // 3. Return the Gateway URL + the extracted filename from the object
      return `${environment.apiGatewayUrl}/api/v1/products/images/${mainImage.imageUrl}`;
    }
    
    // Fallback if no images exist
    return 'assets/placeholder.png'; 
  }
}