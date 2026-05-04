import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { OrderService } from '../../../core/services/order.service';
import { Order } from '../../../core/models/order.model';

@Component({
  selector: 'app-order-detail',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './order-detail.component.html',
  styleUrls: ['./order-detail.component.scss']
})
export class OrderDetailComponent implements OnInit {

  order: Order | null = null;
  isLoading = true;
  errorMessage = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private orderService: OrderService
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (!id) {
      this.router.navigate(['/orders']);
      return;
    }

    this.orderService.getById(id).subscribe({
      next: (order) => {
        this.order = order;
        this.isLoading = false;
      },
      error: () => {
        this.errorMessage = 'Commande introuvable';
        this.isLoading = false;
      }
    });
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