import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Order } from '../models/order.model';

@Injectable({ providedIn: 'root' })
export class OrderService {

  constructor(private http: HttpClient) {}

  getMyOrders(): Observable<Order[]> {
    console.log('getMyOrders called');
    return this.http.get<Order[]>(`${environment.services.orders}/me`);  // ← ajout de /me
  }

  getById(id: string): Observable<Order> {
    return this.http.get<Order>(`${environment.services.orders}/${id}`);
  }
}