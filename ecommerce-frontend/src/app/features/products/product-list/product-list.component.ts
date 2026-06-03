import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ProductService } from '../../../core/services/product.service';
import { CategoryService } from '../../../core/services/category.service';
import { Product } from '../../../core/models/product.model';
import { CategoryNode } from '../../../core/models/category.model';
import { CurrencyMadPipe } from '../../../shared/pipes/currency-mad.pipe';
import { environment } from '../../../../environments/environment';
import { SearchBarComponent } from '../../../shared/components/search-bar/search-bar.component'; 
import { CategoryDropdownComponent } from '../../../shared/components/category-dropdown/category-dropdown.component';

@Component({
  selector: 'app-product-list',
  standalone: true,
  imports: [CommonModule, RouterModule, CurrencyMadPipe, SearchBarComponent, CategoryDropdownComponent], 
  templateUrl: './product-list.component.html',
  styleUrls: ['./product-list.component.scss']
})
export class ProductListComponent implements OnInit {
  allProducts: Product[] = [];
  products: Product[] = [];
  categoryTree: CategoryNode[] = [];
  isLoading = true;
  errorMessage = '';

  constructor(
    private productService: ProductService,
    private categoryService: CategoryService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadAllProducts();
    this.loadCategories();
  }

  loadCategories(): void {
    this.categoryService.getAll().subscribe({
      next: (categories) => {
        this.categoryTree = this.categoryService.buildTree(categories);
        this.cdr.detectChanges();
      }
    });
  }

  loadAllProducts(): void {
    this.isLoading = true;
    this.productService.getAll().subscribe({
      next: (products) => {
        this.allProducts = products;
        this.products = products;
        this.isLoading = false;
        this.cdr.detectChanges(); 
      },
      error: () => {
        this.errorMessage = 'Erreur lors du chargement des produits';
        this.isLoading = false;
        this.cdr.detectChanges();
      }
    });
  }

  onCategoryChange(event: any): void {
    const selectedCategoryId = event.target.value;
    if (!selectedCategoryId) {
      this.products = [...this.allProducts];
    } else {
      this.products = this.allProducts.filter(p => p.categoryId === selectedCategoryId || (p.category && p.category.id === selectedCategoryId) || p.category === selectedCategoryId);
    }
    this.cdr.detectChanges();
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