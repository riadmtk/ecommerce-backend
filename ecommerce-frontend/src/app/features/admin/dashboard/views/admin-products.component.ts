import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { ProductService } from '../../../../core/services/product.service';
import { Product } from '../../../../core/models/product.model';

@Component({
  selector: 'app-admin-products',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './admin-products.component.html',
  styleUrls: ['./admin-products.component.scss']
})
export class AdminProductsComponent implements OnInit {
  products: Product[] = [];
  filteredProducts: Product[] = [];
  searchTerm: string = '';
  isLoading: boolean = false;
  copiedId: string | null = null;

  constructor(private productService: ProductService, private cdr: ChangeDetectorRef) {}

  ngOnInit() {
    this.loadProducts();
  }

  loadProducts() {
    this.isLoading = true;
    this.productService.getAll().subscribe({
      next: (res: Product[]) => {
        const data = (res as any).content || res;
        this.products = data;
        this.filteredProducts = data;
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Erreur chargement produits:', err);
        this.isLoading = false;
        this.cdr.detectChanges();
      }
    });
  }

  filterProducts() {
    const term = this.searchTerm.toLowerCase();
    this.filteredProducts = this.products.filter(p => 
      p.id?.toLowerCase().includes(term) ||
      p.name?.toLowerCase().includes(term)
    );
  }

  increaseStock(product: Product) {
    if (!product.id) return;
    this.productService.increaseStock(product.id, 1).subscribe(() => {
      product.stockQuantity = (product.stockQuantity || 0) + 1;
    });
  }

  decreaseStock(product: Product) {
    if (!product.id || !product.stockQuantity) return;
    this.productService.decreaseStock(product.id, 1).subscribe(() => {
      product.stockQuantity = (product.stockQuantity || 0) - 1;
    });
  }

  softDelete(product: Product) {
    if (!product.id || !confirm('Désactiver ce produit (Soft delete) ?')) return;
    this.productService.delete(product.id).subscribe(() => {
      this.loadProducts();
    });
  }

  hardDelete(product: Product) {
    if (!product.id || !confirm('Supprimer DÉFINITIVEMENT ce produit ? Cette action est irréversible.')) return;
    this.productService.hardDelete(product.id).subscribe(() => {
      this.loadProducts();
    });
  }

  copyId(id: string) {
    navigator.clipboard.writeText(id).then(() => {
      this.copiedId = id;
      this.cdr.detectChanges();
      setTimeout(() => {
        this.copiedId = null;
        this.cdr.detectChanges();
      }, 2000);
    });
  }
}
