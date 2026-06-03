import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { OrderService } from '../../../../core/services/order.service';
import { Order } from '../../../../core/models/order.model';

@Component({
  selector: 'app-admin-orders',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './admin-orders.component.html',
  styleUrls: ['./admin-orders.component.scss']
})
export class AdminOrdersComponent implements OnInit {
  orders: Order[] = [];
  filteredOrders: Order[] = [];
  searchTerm: string = '';
  isLoading: boolean = false;
  copiedId: string | null = null;

  statusOptions = ['PENDING', 'PAID', 'PROCESSING', 'SHIPPED', 'DELIVERED', 'CANCELLED', 'REFUND_REQUESTED', 'REFUNDED'];

  constructor(private orderService: OrderService, private cdr: ChangeDetectorRef) {}

  ngOnInit() {
    this.loadOrders();
  }

  loadOrders() {
    this.isLoading = true;
    this.orderService.getAllOrders().subscribe({
      next: (res: Order[]) => {
        // En cas de réponse paginée du backend (ex: Page<Order>), ajustez res.content si nécessaire
        const data = (res as any).content || res;
        this.orders = data;
        this.filteredOrders = data;
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Erreur chargement commandes:', err);
        this.isLoading = false;
        this.cdr.detectChanges();
      }
    });
  }

  filterOrders() {
    const term = this.searchTerm.toLowerCase();
    this.filteredOrders = this.orders.filter(o => 
      o.id?.toLowerCase().includes(term) ||
      o.userId?.toLowerCase().includes(term) ||
      o.status?.toLowerCase().includes(term)
    );
  }

  updateStatus(order: Order, newStatus: string) {
    if (!order.id) return;
    this.orderService.updateStatus(order.id, newStatus).subscribe((updated: Order) => {
      order.status = updated.status;
    });
  }

  approveRefund(order: Order) {
    if (!order.id) return;
    this.orderService.approveRefund(order.id).subscribe((updated: Order) => {
      order.status = updated.status;
    });
  }

  rejectRefund(order: Order) {
    if (!order.id) return;
    this.orderService.rejectRefund(order.id).subscribe((updated: Order) => {
      order.status = updated.status;
    });
  }

  copyId(id: string) {
    navigator.clipboard.writeText(id).then(() => {
      this.copiedId = id;
      this.cdr.detectChanges();
      setTimeout(() => {
        this.copiedId = null;
        this.cdr.detectChanges();
      }, 2000);
    });
  }
}
