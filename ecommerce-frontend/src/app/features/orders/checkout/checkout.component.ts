import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { OrderService } from '../../../core/services/order.service';
import { CartService } from '../../../core/services/cart.service';

@Component({
  selector: 'app-checkout',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './checkout.component.html',
  styleUrls: ['./checkout.component.scss']
})
export class CheckoutComponent {
  shippingAddress: string = '';
  isLoading = false;
  errorMessage = '';

  constructor(
    private orderService: OrderService,
    private cartService: CartService,
    private router: Router
  ) {}

  placeOrder(): void {
    if (!this.shippingAddress.trim()) {
      this.errorMessage = 'L\'adresse de livraison est obligatoire.';
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';

    this.orderService.createOrder(this.shippingAddress).subscribe({
      next: (order) => {
        // La commande a été créée, naviguer vers le paiement
        this.router.navigate(['/payment-checkout'], { queryParams: { orderId: order.id } });
      },
      error: (err) => {
        console.error('Erreur création commande', err);
        this.errorMessage = 'Erreur lors de la création de la commande. Veuillez réessayer.';
        this.isLoading = false;
      }
    });
  }
}