import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { OrderService } from '../../../core/services/order.service';
import { PaymentService } from '../../../core/services/payment.service';
import { Order } from '../../../core/models/order.model';
import { Payment } from '../../../core/models/payment.model';
import { AuthService } from '../../../core/auth/auth.service';
import { CurrencyMadPipe } from '../../../shared/pipes/currency-mad.pipe';
import { Observable, map } from 'rxjs';

@Component({
  selector: 'app-order-detail',
  standalone: true,
  imports: [CommonModule, RouterModule, CurrencyMadPipe],
  templateUrl: './order-detail.component.html',
  styleUrls: ['./order-detail.component.scss']
})
export class OrderDetailComponent implements OnInit {

  order: Order | null = null;
  payment: Payment | null = null;
  isLoading = true;
  errorMessage = '';
  isAdmin$: Observable<boolean>;
  isRefunding = false;
  refundSuccess = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private orderService: OrderService,
    private paymentService: PaymentService,
    private authService: AuthService,
    private cdr: ChangeDetectorRef
  ) {
    this.isAdmin$ = this.authService.currentUser$.pipe(
      map(user => user?.role === 'ADMIN')
    );
  }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (!id) { this.router.navigate(['/orders']); return; }
    this.loadOrder(id);
  }

  loadOrder(id: string): void {
    this.isLoading = true;
    this.orderService.getById(id).subscribe({
      next: (order) => {
        this.order = order;
        this.isLoading = false;
        this.cdr.detectChanges();
        this.loadPayment(order.id);
      },
      error: () => {
        this.errorMessage = 'Commande introuvable';
        this.isLoading = false;
        this.cdr.detectChanges();
      }
    });
  }

  private loadPayment(orderId: string): void {
    this.paymentService.getByOrderId(orderId).subscribe({
      next: (payment) => {
        this.payment = payment;
        this.cdr.detectChanges();
      },
      error: () => {}
    });
  }

  changeStatus(newStatus: string): void {
    if (!this.order) return;
    this.orderService.updateStatus(this.order.id, newStatus).subscribe({
      next: (updatedOrder) => {
        this.order = updatedOrder;
        this.cdr.detectChanges();
      },
      error: () => this.errorMessage = 'Erreur lors du changement de statut'
    });
  }

  cancelOrder(): void {
    if (!this.order) return;
    this.orderService.cancelOrder(this.order.id).subscribe({
      next: (updatedOrder) => {
        this.order = updatedOrder;
        this.cdr.detectChanges();
      },
      error: () => this.errorMessage = 'Erreur lors de l\'annulation'
    });
  }

  deliverOrder(): void {
    if (!this.order) return;
    this.orderService.deliverOrder(this.order.id).subscribe({
      next: (updatedOrder) => {
        this.order = updatedOrder;
        this.cdr.detectChanges();
      },
      error: () => this.errorMessage = 'Erreur lors de la confirmation de livraison'
    });
  }

  /*requestRefund(): void {
    if (!this.payment) return;
    this.isRefunding = true;
    this.paymentService.refund(this.payment.id).subscribe({
      next: (refunded) => {
        this.payment = refunded;
        this.refundSuccess = true;
        this.isRefunding = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.isRefunding = false;
        this.cdr.detectChanges();
      }
    });
  }*/

  canCancel(status: string): boolean {
    return !['SHIPPED', 'DELIVERED', 'CANCELLED', 'REFUND_REQUESTED', 'REFUNDED'].includes(status);
  }

  canRefund(): boolean {
    return this.payment?.status === 'SUCCESS' && !this.refundSuccess;
  }

  canPay(): boolean {
    return this.order?.status === 'PENDING' &&
           (!this.payment || this.payment.status === 'FAILED');
  }

  getStatusLabel(status: string): string {
    const labels: Record<string, string> = {
      PENDING: 'En attente',
      PAID: 'Payée',
      CONFIRMED: 'Confirmée',
      PROCESSING: 'En préparation',
      SHIPPED: 'Expédiée',
      DELIVERED: 'Livrée',
      CANCELLED: 'Annulée',
      REFUND_REQUESTED: 'Remboursement demandé',
      REFUNDED: 'Remboursée'
    };
    return labels[status] || status;
  }

  getStatusClass(status: string): string {
    const classes: Record<string, string> = {
      PENDING: 'status-pending',
      PAID: 'status-paid',
      CONFIRMED: 'status-confirmed',
      PROCESSING: 'status-processing',
      SHIPPED: 'status-shipped',
      DELIVERED: 'status-delivered',
      CANCELLED: 'status-cancelled',
      REFUND_REQUESTED: 'status-refund-requested',
      REFUNDED: 'status-refunded'
    };
    return classes[status] || '';
  }

  getPaymentStatusLabel(status: string): string {
    const labels: Record<string, string> = {
      PENDING: '🕐 En attente',
      SUCCESS: '✅ Payé',
      FAILED: '❌ Échoué',
      REFUNDED: '↩️ Remboursé'
    };
    return labels[status] || status;
  }

  getPaymentStatusClass(status: string): string {
    const classes: Record<string, string> = {
      PENDING: 'pay-pending',
      SUCCESS: 'pay-success',
      FAILED: 'pay-failed',
      REFUNDED: 'pay-refunded'
    };
    return classes[status] || '';
  }

  requestRefund(): void {
    this.orderService.requestRefund(this.order!.id).subscribe({
        next: (updatedOrder) => {
            this.order = updatedOrder;
            this.cdr.detectChanges();
        },
        error: () => this.errorMessage = 'Erreur lors de la demande'
    });
  }

  approveRefund(): void {
      this.orderService.approveRefund(this.order!.id).subscribe({
          next: (updatedOrder) => {
              this.order = updatedOrder;
              this.cdr.detectChanges();
          },
          error: () => {
            this.errorMessage = 'Erreur lors de l\'approbation';
            this.cdr.detectChanges();
        }
      });
  }

  rejectRefund(): void {
      this.orderService.rejectRefund(this.order!.id).subscribe({
          next: (updatedOrder) => {
              this.order = updatedOrder;
              this.cdr.detectChanges();
          },
          error: () => this.errorMessage = 'Erreur lors du rejet'
      });
  }
}