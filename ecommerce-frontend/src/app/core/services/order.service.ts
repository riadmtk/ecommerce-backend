import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Order } from '../models/order.model';

@Injectable({ providedIn: 'root' })
export class OrderService {

  constructor(private http: HttpClient) { }

  createOrder(shippingAddress: string): Observable<Order> {
    return this.http.post<Order>(`${environment.services.orders}`, { shippingAddress });
  }

  getMyOrders(): Observable<Order[]> {
    console.log('getMyOrders called');
    return this.http.get<Order[]>(`${environment.services.orders}/me`);  // ← ajout de /me
  }

  getById(id: string): Observable<Order> {
    return this.http.get<Order>(`${environment.services.orders}/${id}`);
  }

  getAllOrders(): Observable<Order[]> {
    return this.http.get<Order[]>(`${environment.services.orders}/all`);
  }
  deliverOrder(orderId: string): Observable<Order> {
    return this.http.post<Order>(`${environment.services.orders}/${orderId}/deliver`, {});
  }

  updateStatus(orderId: string, newStatus: string): Observable<Order> {
    return this.http.patch<Order>(`${environment.services.orders}/${orderId}/status`, { newStatus });
  }

  cancelOrder(orderId: string): Observable<Order> {
    return this.http.post<Order>(`${environment.services.orders}/${orderId}/cancel`, {});
  }

  /*requestRefund(orderId: string): Observable<Order> {
    return this.http.post<Order>(`${environment.services.orders}/${orderId}/request-refund`, {});
  }*/

  requestRefund(orderId: string, reason: string): Observable<Order> {
    return this.http.post<Order>(`${environment.services.orders}/${orderId}/refund-request`, { reason });
  }
  
  approveRefund(orderId: string): Observable<Order> {
      return this.http.post<Order>(`${environment.services.orders}/${orderId}/approve-refund`, {});
  }

  rejectRefund(orderId: string): Observable<Order> {
      return this.http.post<Order>(`${environment.services.orders}/${orderId}/reject-refund`, {});
  }
}