import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';

@Component({
  selector: 'app-paypal-cancel',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './paypal-cancel.component.html',
  styleUrls: ['./paypal-cancel.component.scss']
})
export class PaypalCancelComponent implements OnInit {

  orderId: string | null = null;

  constructor(private router: Router) {}

  ngOnInit(): void {
    // Récupérer l'orderId pour permettre de réessayer
    this.orderId = sessionStorage.getItem('paypal_order_id');
    // Ne pas supprimer — l'utilisateur peut retenter
  }

  retryPayment(): void {
    if (this.orderId) {
      this.router.navigate(['/payment-checkout'], {
        queryParams: { orderId: this.orderId }
      });
    } else {
      this.router.navigate(['/orders']);
    }
  }
}