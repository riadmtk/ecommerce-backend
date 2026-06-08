import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { OrderService } from '../../../../core/services/order.service';
import { UserService } from '../../../../core/services/user.service';
import { ProductService } from '../../../../core/services/product.service';
import { forkJoin } from 'rxjs';
import { CurrencyMadPipe } from '../../../../shared/pipes/currency-mad.pipe';

@Component({
  selector: 'app-admin-overview',
  standalone: true,
  imports: [CommonModule, CurrencyMadPipe],
  templateUrl: './admin-overview.component.html',
  styleUrls: ['./admin-overview.component.scss']
})
export class AdminOverviewComponent implements OnInit {
  isLoading = true;

  // KPIs
  totalOrders = 0;
  totalRevenue = 0;
  totalUsers = 0;
  activeProducts = 0;

  // Recent Activity
  recentOrders: any[] = [];

  // Chart Data
  monthlyOrders: { month: string, count: number }[] = [];
  maxChartValue = 0;

  constructor(
    private orderService: OrderService,
    private userService: UserService,
    private productService: ProductService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.fetchData();
  }

  fetchData() {
    forkJoin({
      orders: this.orderService.getAllOrders(),
      users: this.userService.getAllUsers(),
      products: this.productService.getAll()
    }).subscribe({
      next: (data) => {
        // Aggregate Orders
        this.totalOrders = data.orders.length;
        this.totalRevenue = data.orders.reduce((sum: number, order: any) => sum + (order.totalAmount || 0), 0);
        
        // Aggregate Users
        this.totalUsers = data.users.length;

        // Aggregate Products
        this.activeProducts = data.products.filter(p => p.stockQuantity && p.stockQuantity > 0).length;

        // Recent Orders
        this.recentOrders = [...data.orders]
          .sort((a: any, b: any) => new Date(b.createdAt!).getTime() - new Date(a.createdAt!).getTime())
          .slice(0, 5);

        // Chart Data (Last 6 months)
        this.calculateChartData(data.orders);

        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.isLoading = false;
        this.cdr.detectChanges();
      }
    });
  }

  calculateChartData(orders: any[]) {
    const months = ['Jan', 'Fév', 'Mar', 'Avr', 'Mai', 'Juin', 'Juil', 'Aoû', 'Sep', 'Oct', 'Nov', 'Déc'];
    const now = new Date();
    const dataPoints: { month: string, year: number, monthIndex: number, count: number }[] = [];

    // Initialize last 6 months
    for (let i = 5; i >= 0; i--) {
      const d = new Date(now.getFullYear(), now.getMonth() - i, 1);
      dataPoints.push({
        month: months[d.getMonth()],
        year: d.getFullYear(),
        monthIndex: d.getMonth(),
        count: 0
      });
    }

    // Count orders
    orders.forEach(order => {
      if (!order.createdAt) return;
      const orderDate = new Date(order.createdAt);
      const point = dataPoints.find(p => p.monthIndex === orderDate.getMonth() && p.year === orderDate.getFullYear());
      if (point) {
        point.count++;
      }
    });

    this.monthlyOrders = dataPoints.map(p => ({ month: p.month, count: p.count }));
    this.maxChartValue = Math.max(...this.monthlyOrders.map(m => m.count), 5); // Minimum y-axis max of 5
  }

  // SVG Chart helpers
  getChartPoints(): string {
    if (!this.monthlyOrders.length) return '';
    return this.monthlyOrders.map((data, index) => {
      return `${this.getPointX(index)},${this.getPointY(data.count)}`;
    }).join(' ');
  }

  getAreaPoints(): string {
    if (!this.monthlyOrders.length) return '';
    const points = this.getChartPoints();
    const firstX = this.getPointX(0);
    const lastX = this.getPointX(this.monthlyOrders.length - 1);
    const bottomY = 180; // height (200) - paddingY (20)
    return `${firstX},${bottomY} ${points} ${lastX},${bottomY}`;
  }

  getPointY(count: number): number {
    const height = 200;
    const paddingY = 20;
    const usableHeight = height - (paddingY * 2);
    return height - paddingY - ((count / this.maxChartValue) * usableHeight);
  }

  getPointX(index: number): number {
    const width = 600;
    const paddingX = 40;
    const usableWidth = width - (paddingX * 2);
    return paddingX + (index * (usableWidth / 5));
  }
}
