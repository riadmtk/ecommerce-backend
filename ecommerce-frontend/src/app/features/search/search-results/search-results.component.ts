import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { SearchService } from '../../../core/services/search.service';
import { Product } from '../../../core/models/product.model';
import { CurrencyMadPipe } from '../../../shared/pipes/currency-mad.pipe';
import { environment } from '../../../../environments/environment';
import { SearchBarComponent } from '../../../shared/components/search-bar/search-bar.component';

@Component({
  selector: 'app-search-results',
  standalone: true,
  imports: [CommonModule, RouterModule, CurrencyMadPipe, SearchBarComponent],
  templateUrl: './search-results.component.html',
  styleUrls: ['./search-results.component.scss'] // Just copy your product-list.component.scss here!
})
export class SearchResultsComponent implements OnInit {
  products: Product[] = [];
  isLoading = true;
  errorMessage = '';
  currentQuery = '';

  constructor(
    private route: ActivatedRoute,
    private searchService: SearchService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    // Listen to changes in the URL query parameters
    this.route.queryParams.subscribe(params => {
      this.currentQuery = params['q'] || '';
      
      if (this.currentQuery) {
        this.performSearch(this.currentQuery);
      } else {
        this.products = [];
        this.isLoading = false;
      }
    });
  }

  performSearch(query: string): void {
    this.isLoading = true;
    this.errorMessage = '';
    
    this.searchService.searchProducts(query).subscribe({
      next: (response) => {
        this.products = response.results;
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.errorMessage = 'Erreur lors de la recherche. Veuillez réessayer.';
        this.isLoading = false;
        this.cdr.detectChanges();
      }
    });
  }

  // Same trusty image helper!
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