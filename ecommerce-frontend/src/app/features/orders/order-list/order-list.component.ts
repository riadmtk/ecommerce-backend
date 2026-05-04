import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { OrderService } from '../../../core/services/order.service';
import { Order } from '../../../core/models/order.model';

@Component({
  selector: 'app-order-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './order-list.component.html',
  styleUrls: ['./order-list.component.scss']
})
export class OrderListComponent implements OnInit {

  orders: Order[] = [];
  isLoading = true;
  errorMessage = '';

  constructor(
    private orderService: OrderService,
    private cdr: ChangeDetectorRef) {}

  ngOnInit(): void {
    console.log('ngOnInit started');
    this.orderService.getMyOrders().subscribe({
      next: (orders) => {
        console.log('Orders received', orders);
        this.orders = orders;
        this.isLoading = false;
        this.cdr.detectChanges(); // ← force Angular à vérifier les changements
      },
      error: (err) => {
        console.log('Error in getMyOrders', err);
        this.errorMessage = 'Erreur lors du chargement des commandes';
        this.isLoading = false;
      }
    });
    console.log('ngOnInit finished');
  }

  getStatusLabel(status: string): string {
    const labels: Record<string, string> = {
      PENDING: 'En attente',
      CONFIRMED: 'Confirmée',
      SHIPPED: 'Expédiée',
      DELIVERED: 'Livrée',
      REFUNDED: 'Remboursée'
    };
    return labels[status] || status;
  }

  getStatusClass(status: string): string {
    const classes: Record<string, string> = {
      PENDING: 'status-pending',
      CONFIRMED: 'status-confirmed',
      SHIPPED: 'status-shipped',
      DELIVERED: 'status-delivered',
      REFUNDED: 'status-refunded'
    };
    return classes[status] || '';
  }
}