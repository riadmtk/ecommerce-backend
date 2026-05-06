import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { CartService } from '../../core/services/cart.service';
import { CartItem } from '../../core/models/cart.model';
import { CurrencyMadPipe } from '../../shared/pipes/currency-mad.pipe';

@Component({
    selector: 'app-cart',
    standalone: true,
    imports: [CommonModule, RouterModule, CurrencyMadPipe, FormsModule],
    templateUrl: './cart.component.html',
    styleUrls: ['./cart.component.scss']
})
export class CartComponent implements OnInit {
    items: CartItem[] = [];
    isLoading = true;
    errorMessage = '';

    constructor(private cartService: CartService, private cdr: ChangeDetectorRef, private router: Router) { }

    ngOnInit(): void {
        this.loadCart();
    }

    loadCart(): void {
        this.isLoading = true;
        this.errorMessage = '';
        this.cartService.getEnrichedCart().subscribe({
            next: ({ itemsDetailed }) => {
                this.items = itemsDetailed;
                this.isLoading = false;
                this.cdr.detectChanges();
            },
            error: () => {
                this.errorMessage = 'Impossible de charger le panier';
                this.isLoading = false;
                this.cdr.detectChanges();
            }
        });
    }

    removeItem(productId: string): void {
        this.cartService.removeItem(productId).subscribe({
            next: () => this.loadCart(),
            error: () => this.errorMessage = 'Erreur lors de la suppression'
        });
    }

    clearCart(): void {
        this.cartService.clearCart().subscribe({
            next: () => {
                this.items = [];
                this.cdr.detectChanges();
            },
            error: () => this.errorMessage = 'Erreur lors du vidage du panier'
        });
    }

    get total(): number {
        return this.items.reduce((sum, item) => sum + (item.unitPrice || 0) * item.quantity, 0);
    }

    // Méthode générique pour mettre à jour la quantité
    updateQuantity(productId: string, newQuantity: number): void {
        if (newQuantity <= 0) {
            this.removeItem(productId);
            return;
        }
        this.cartService.updateItemQuantity(productId, newQuantity).subscribe({
            next: () => this.loadCart(),
            error: () => this.errorMessage = 'Erreur lors de la mise à jour'
        });
    }

    increaseQuantity(productId: string, currentQuantity: number): void {
        this.updateQuantity(productId, currentQuantity + 1);
    }

    decreaseQuantity(productId: string, currentQuantity: number): void {
        if (currentQuantity <= 1) {
            this.removeItem(productId);
            return;
        }
        this.updateQuantity(productId, currentQuantity - 1);
    }

    goToCheckout(): void {
        this.router.navigate(['/checkout']);
    }

    /*checkout(): void {
        alert('Fonctionnalité de commande à venir');
    }*/
}