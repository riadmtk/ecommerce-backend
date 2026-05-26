import { Component, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, ActivatedRoute, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { PaymentService } from '../../core/services/payment.service';
import { OrderService } from '../../core/services/order.service';
import { Order } from '../../core/models/order.model';
import { PaymentCreatedResponse } from '../../core/models/payment.model';
import { loadStripe, Stripe, StripeElements, StripePaymentElement } from '@stripe/stripe-js';
import { environment } from '../../../environments/environment';
import { CurrencyMadPipe } from '../../shared/pipes/currency-mad.pipe';

@Component({
  selector: 'app-checkout',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, CurrencyMadPipe],
  templateUrl: './checkout.component.html',
  styleUrls: ['./checkout.component.scss']
})
export class CheckoutComponent implements OnInit, OnDestroy {

  order: Order | null = null;
  payment: PaymentCreatedResponse | null = null;

  selectedMethod: 'STRIPE' | 'PAYPAL' = 'STRIPE';
  isLoading = true;
  isProcessing = false;
  errorMessage = '';
  successMessage = '';

  // Stripe
  private stripe: Stripe | null = null;
  private elements: StripeElements | null = null;
  private paymentElement: StripePaymentElement | null = null;
  stripeReady = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private paymentService: PaymentService,
    private orderService: OrderService,
    private cdr: ChangeDetectorRef
  ) {}

  async ngOnInit(): Promise<void> {
    const orderId = this.route.snapshot.queryParamMap.get('orderId');
    if (!orderId) {
      this.router.navigate(['/orders']);
      return;
    }
    this.loadOrder(orderId);
  }

  ngOnDestroy(): void {
    this.paymentElement?.destroy();
  }

  private loadOrder(orderId: string): void {
    this.orderService.getById(orderId).subscribe({
      next: (order) => {
        this.order = order;
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.errorMessage = 'Commande introuvable';
        this.isLoading = false;
        this.cdr.detectChanges();
      }
    });
  }

  initiatePayment(): void {
    if (!this.order) return;

    this.isProcessing = true;
    this.errorMessage = '';
    this.cdr.detectChanges();

    this.paymentService.initiate({
      orderId: this.order.id,
      amount: this.order.totalAmount,
      currency: 'EUR',
      paymentMethod: this.selectedMethod
    }).subscribe({
      next: (payment) => {
        this.payment = payment;
        // Forcer la mise à jour du DOM pour que le conteneur #stripe-payment-element existe
        this.cdr.detectChanges();
        if (this.selectedMethod === 'STRIPE') {
          // Laisser Angular créer l'élément dans le DOM avant de monter Stripe
          setTimeout(() => this.initStripe(payment.clientSecret), 0);
        } else {
          this.isProcessing = false;
        }
      },
      error: (err) => {
        this.errorMessage = 'Erreur lors de l\'initiation du paiement';
        this.isProcessing = false;
        this.cdr.detectChanges();
      }
    });
  }

  private async initStripe(clientSecret: string): Promise<void> {
    // Charger Stripe une seule fois
    if (!this.stripe) {
      this.stripe = await loadStripe(environment.stripePublicKey);
      if (!this.stripe) {
        this.errorMessage = 'Erreur chargement Stripe';
        this.isProcessing = false;
        this.cdr.detectChanges();
        return;
      }
    }

    this.elements = this.stripe.elements({
      clientSecret,
      appearance: {
        theme: 'stripe',
        variables: {
          colorPrimary: '#4f46e5',
          borderRadius: '8px'
        }
      }
    });

    this.paymentElement = this.elements.create('payment');
    this.paymentElement.mount('#stripe-payment-element');
    this.stripeReady = true;
    this.isProcessing = false;
    this.cdr.detectChanges();
  }

  async confirmStripePayment(): Promise<void> {
    if (!this.stripe || !this.elements || !this.payment) return;

    this.isProcessing = true;
    this.errorMessage = '';
    this.cdr.detectChanges();

    const { error } = await this.stripe.confirmPayment({
        elements: this.elements,
        confirmParams: {
            return_url: `${window.location.origin}/payment-success?orderId=${this.order?.id}`
        },
        redirect: 'if_required'
    });

    if (error) {
        this.errorMessage = error.message || 'Paiement refusé';
        this.isProcessing = false;
        this.cdr.detectChanges();
    } else {
        // Le paiement Stripe est réussi → confirmer côté backend
        this.paymentService.confirmPayment(this.payment.id).subscribe({
            next: () => {
                this.successMessage = 'Paiement confirmé !';
                this.cdr.detectChanges();
                setTimeout(() => this.router.navigate(['/orders', this.order?.id]), 2000);
            },
            error: () => {
                this.errorMessage = 'Erreur lors de la confirmation du paiement';
                this.isProcessing = false;
                this.cdr.detectChanges();
            }
        });
    }
  }

  confirmPaypalPayment(): void {
    if (!this.payment?.clientSecret) return;

    // Sauvegarder les IDs avant de quitter Angular (window.location.href perd le state)
    sessionStorage.setItem('paypal_payment_id', this.payment.id);
    sessionStorage.setItem('paypal_order_id', this.order?.id ?? '');

    // Rediriger vers l'URL d'approbation PayPal (= clientSecret)
    window.location.href = this.payment.clientSecret;
  }

  getStatusLabel(status: string): string {
    const labels: Record<string, string> = {
      PENDING: 'En attente',
      PAID: 'Payée',
      CONFIRMED: 'Confirmée',
      SHIPPED: 'Expédiée',
      DELIVERED: 'Livrée',
      CANCELLED: 'Annulée'
    };
    return labels[status] || status;
  }
}