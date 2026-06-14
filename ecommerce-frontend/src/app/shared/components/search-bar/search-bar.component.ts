import { Component, OnInit, OnDestroy, HostListener, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ProductService } from '../../../core/services/product.service';
import { Product } from '../../../core/models/product.model';
import { Subject, Subscription } from 'rxjs';
import { debounceTime, distinctUntilChanged, switchMap } from 'rxjs/operators';
import { environment } from '../../../../environments/environment';
import { CurrencyMadPipe } from '../../pipes/currency-mad.pipe';

@Component({
  selector: 'app-search-bar',
  standalone: true,
  imports: [CommonModule, FormsModule, CurrencyMadPipe],
  templateUrl: './search-bar.component.html',
  styleUrls: ['./search-bar.component.scss'] 
})
export class SearchBarComponent implements OnInit, OnDestroy {
  searchTerm: string = '';
  suggestions: Product[] = [];
  showSuggestions: boolean = false;
  
  private searchSubject = new Subject<string>();
  private searchSubscription!: Subscription;

  constructor(
    private router: Router, 
    private productService: ProductService,
    private eRef: ElementRef
  ) {}

  ngOnInit(): void {
    this.searchSubscription = this.searchSubject.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      switchMap(term => {
        if (!term.trim()) {
          return [];
        }
        // Assuming getAll can be used or there is a search endpoint. 
        // We'll fetch all and filter for now to guarantee it works without backend changes.
        return this.productService.getAll();
      })
    ).subscribe((products: Product[]) => {
      const term = this.searchTerm.trim().toLowerCase();
      if (!term) {
        this.suggestions = [];
        this.showSuggestions = false;
        return;
      }
      this.suggestions = products
        .filter(p => p.name.toLowerCase().includes(term) || (p.description && p.description.toLowerCase().includes(term)))
        .slice(0, 5); // top 5
      
      this.showSuggestions = this.suggestions.length > 0;
    });
  }

  ngOnDestroy(): void {
    if (this.searchSubscription) {
      this.searchSubscription.unsubscribe();
    }
  }

  onInput(): void {
    this.searchSubject.next(this.searchTerm);
    if (!this.searchTerm.trim()) {
      this.showSuggestions = false;
    }
  }

  onSearch(): void {
    if (this.searchTerm.trim()) {
      this.showSuggestions = false;
      this.router.navigate(['/search'], { queryParams: { q: this.searchTerm.trim() } });
    }
  }

  selectSuggestion(product: Product): void {
    this.showSuggestions = false;
    this.searchTerm = '';
    this.router.navigate(['/products', product.id]);
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

  @HostListener('document:click', ['$event'])
  clickout(event: Event) {
    if (!this.eRef.nativeElement.contains(event.target)) {
      this.showSuggestions = false;
    }
  }
}