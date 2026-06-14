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
  imports: [CommonModule, RouterModule, CurrencyMadPipe], 
  templateUrl: './product-list.component.html',
  styleUrls: ['./product-list.component.scss']
})
export class ProductListComponent implements OnInit {
  allProducts: Product[] = [];
  products: Product[] = [];
  categoryTree: CategoryNode[] = [];
  isLoading = true;
  errorMessage = '';

  // View Options
  viewMode: 'grid' | 'large' | 'list' = 'grid';

  // Sidebar Accordion State
  expandedNodes: Set<string> = new Set();

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

  onCategoryChange(categoryId: string | undefined): void {
    if (!categoryId) {
      this.products = [...this.allProducts];
    } else {
      this.products = this.allProducts.filter(p => p.categoryId === categoryId || (p.category && p.category.id === categoryId) || p.category === categoryId);
    }
    this.cdr.detectChanges();
  }

  toggleNode(nodeId: string, event: Event): void {
    event.stopPropagation();
    if (this.expandedNodes.has(nodeId)) {
      this.expandedNodes.delete(nodeId);
    } else {
      this.expandedNodes.add(nodeId);
    }
    this.cdr.detectChanges();
  }

  setViewMode(mode: 'grid' | 'large' | 'list'): void {
    this.viewMode = mode;
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