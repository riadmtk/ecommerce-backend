import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { OrderService } from '../../../core/services/order.service';
import { Order } from '../../../core/models/order.model';
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
  isLoading = true;
  errorMessage = '';
  isAdmin$: Observable<boolean>;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private orderService: OrderService,
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
      },
      error: () => {
        this.errorMessage = 'Commande introuvable';
        this.isLoading = false;
        this.cdr.detectChanges();
      }
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

  canCancel(status: string): boolean {
    return !['SHIPPED', 'DELIVERED', 'CANCELLED', 'REFUNDED'].includes(status);
  }

  getStatusLabel(status: string): string {
    const labels: Record<string, string> = {
      PENDING: 'En attente',
      PAID: 'Payée',
      PROCESSING: 'En préparation',
      SHIPPED: 'Expédiée',
      DELIVERED: 'Livrée',
      CANCELLED: 'Annulée',
      REFUNDED: 'Remboursée'
    };
    return labels[status] || status;
  }

  getStatusClass(status: string): string {
    const classes: Record<string, string> = {
      PENDING: 'status-pending', PAID: 'status-paid', PROCESSING: 'status-processing',
      SHIPPED: 'status-shipped', DELIVERED: 'status-delivered',
      CANCELLED: 'status-cancelled', REFUNDED: 'status-refunded'
    };
    return classes[status] || '';
  }
}