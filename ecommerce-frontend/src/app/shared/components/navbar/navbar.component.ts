import { Component, OnInit, OnDestroy, HostListener, ElementRef, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { AuthService } from '../../../core/auth/auth.service';
import { CategoryService } from '../../../core/services/category.service';
import { CategoryNode } from '../../../core/models/category.model';
import { CartService } from '../../../core/services/cart.service';
import { CartItem } from '../../../core/models/cart.model';
import { Observable, Subscription, map } from 'rxjs';
import { SearchBarComponent } from '../search-bar/search-bar.component';
import { CurrencyMadPipe } from '../../../shared/pipes/currency-mad.pipe';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule, SearchBarComponent, CurrencyMadPipe],
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss']
})
export class NavbarComponent implements OnInit, OnDestroy {

  isAdmin$: Observable<boolean>;
  categories: CategoryNode[] = [];
  isCategoryDropdownOpen = false;

  // Cart State
  cartItemCount: number = 0;
  isCartDrawerOpen = false;
  cartItemsDetailed: CartItem[] = [];
  cartTotal: number = 0;
  private cartSub!: Subscription;

  constructor(
    public authService: AuthService,
    private categoryService: CategoryService,
    private cartService: CartService,
    private router: Router,
    private eRef: ElementRef,
    private cdr: ChangeDetectorRef
  ) {
    this.isAdmin$ = this.authService.currentUser$.pipe(
      map(user => user?.role === 'ADMIN')
    );
  }

  ngOnInit() {
    this.categoryService.getAll().subscribe(cats => {
      this.categories = this.categoryService.buildTree(cats);
      this.cdr.detectChanges();
    });

    // Subscribe to cart updates globally
    this.cartSub = this.cartService.cartUpdated$.subscribe(() => {
      if (this.authService.isLoggedIn()) {
        this.fetchCartCount();
        if (this.isCartDrawerOpen) {
          this.fetchCartDetails();
        }
      }
    });

    // Listen to user login/logout to reset or fetch cart
    this.authService.currentUser$.subscribe(user => {
      if (user) {
        this.fetchCartCount();
      } else {
        // Reset state on logout
        this.cartItemCount = 0;
        this.cartItemsDetailed = [];
        this.cartTotal = 0;
        this.isCartDrawerOpen = false;
      }
      this.cdr.detectChanges();
    });
  }

  ngOnDestroy() {
    if (this.cartSub) {
      this.cartSub.unsubscribe();
    }
  }

  fetchCartCount() {
    this.cartService.getCart().subscribe(cart => {
      this.cartItemCount = cart.items.reduce((acc, item) => acc + item.quantity, 0);
      this.cdr.detectChanges();
    });
  }

  fetchCartDetails() {
    this.cartService.getEnrichedCart().subscribe(({ cart, itemsDetailed }) => {
      this.cartItemsDetailed = itemsDetailed;
      this.cartTotal = itemsDetailed.reduce((total, item) => total + ((item.unitPrice || 0) * item.quantity), 0);
      this.cdr.detectChanges();
    });
  }

  toggleCartDrawer() {
    if (!this.authService.isLoggedIn()) {
      this.router.navigate(['/auth/login']);
      return;
    }
    this.isCartDrawerOpen = !this.isCartDrawerOpen;
    if (this.isCartDrawerOpen) {
      this.fetchCartDetails();
    }
    this.cdr.detectChanges();
  }

  closeCartDrawer() {
    this.isCartDrawerOpen = false;
    this.cdr.detectChanges();
  }

  toggleCategoryDropdown() {
    this.isCategoryDropdownOpen = !this.isCategoryDropdownOpen;
    this.cdr.detectChanges();
  }

  selectCategory(categoryId: string) {
    this.isCategoryDropdownOpen = false;
    this.router.navigate(['/products'], { queryParams: { category: categoryId } });
    this.cdr.detectChanges();
  }

  @HostListener('document:click', ['$event'])
  clickout(event: Event) {
    // We do not close the cart drawer here to avoid issues with deleting items, etc.
    // The overlay click handles closing the drawer.
    if (!this.eRef.nativeElement.contains(event.target)) {
      if (this.isCategoryDropdownOpen) {
        this.isCategoryDropdownOpen = false;
        this.cdr.detectChanges();
      }
    }
  }

  logout(): void {
    this.authService.logout();
    this.cdr.detectChanges();
  }
}