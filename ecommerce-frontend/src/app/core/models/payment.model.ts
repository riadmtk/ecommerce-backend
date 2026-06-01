export interface Payment {
  id: string;
  orderId: string;
  userId: string;
  amount: number;
  currency: string;
  paymentMethod: 'STRIPE' | 'PAYPAL';
  status: 'PENDING' | 'SUCCESS' | 'FAILED' | 'REFUNDED';
  transactionId?: string;
  failureReason?: string;
  createdAt: string;
}

export interface PaymentCreatedResponse extends Payment {
  clientSecret: string;
}

export interface InitiatePaymentRequest {
  orderId: string;
  amount: number;
  currency: string;
  paymentMethod: 'STRIPE' | 'PAYPAL';
}