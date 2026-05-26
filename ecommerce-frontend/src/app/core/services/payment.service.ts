import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  Payment,
  PaymentCreatedResponse,
  InitiatePaymentRequest
} from '../models/payment.model';

@Injectable({ providedIn: 'root' })
export class PaymentService {

  private baseUrl = `${environment.apiGatewayUrl}/api/payments`;

  constructor(private http: HttpClient) {}

  initiate(request: InitiatePaymentRequest): Observable<PaymentCreatedResponse> {
    return this.http.post<PaymentCreatedResponse>(this.baseUrl, request);
  }

  getById(id: string): Observable<Payment> {
    return this.http.get<Payment>(`${this.baseUrl}/${id}`);
  }

  getByOrderId(orderId: string): Observable<Payment> {
    return this.http.get<Payment>(`${this.baseUrl}/order/${orderId}`);
  }

  refund(id: string): Observable<Payment> {
    return this.http.post<Payment>(`${this.baseUrl}/${id}/refund`, {});
  }

  confirmPayment(paymentId: string): Observable<void> {
    return this.http.post<void>(`${environment.services.payments}/${paymentId}/confirm`, {});
  }
}