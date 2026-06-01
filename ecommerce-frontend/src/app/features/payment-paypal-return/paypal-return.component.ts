import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { PaymentService } from '../../core/services/payment.service';

@Component({
  selector: 'app-paypal-return',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './paypal-return.component.html',
  styleUrls: ['./paypal-return.component.scss']
})
export class PaypalReturnComponent implements OnInit {

  status: 'loading' | 'success' | 'error' = 'loading';
  errorMessage = '';
  orderId: string | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private paymentService: PaymentService
  ) {}

  ngOnInit(): void {
    // PayPal renvoie ?token=PAYPAL_ORDER_ID&PayerID=...
    const token = this.route.snapshot.queryParamMap.get('token');

    // Récupérer les IDs stockés avant la redirection
    const paymentId = sessionStorage.getItem('paypal_payment_id');
    this.orderId   = sessionStorage.getItem('paypal_order_id');

    if (!token || !paymentId) {
      this.status = 'error';
      this.errorMessage = 'Informations de paiement manquantes.';
      return;
    }

    // Confirmer le paiement côté backend
    this.paymentService.confirmPayment(paymentId).subscribe({
      next: () => {
        // Nettoyer sessionStorage
        sessionStorage.removeItem('paypal_payment_id');
        sessionStorage.removeItem('paypal_order_id');

        this.status = 'success';

        // Redirection vers payment-success après 2s
        setTimeout(() => {
          this.router.navigate(['/payment-success'], {
            queryParams: { orderId: this.orderId }
          });
        }, 2000);
      },
      error: (err) => {
        this.status = 'error';
        this.errorMessage = 'Erreur lors de la confirmation du paiement PayPal.';
      }
    });
  }
}