export type PaymentStatus =
  | 'INITIATED'
  | 'PENDING'
  | 'SUCCESS'
  | 'FAILED'
  | 'REFUNDED'
  | 'PARTIALLY_REFUNDED'
  | 'CANCELLED';

export type PaymentMethod =
  | 'UPI'
  | 'CREDIT_CARD'
  | 'DEBIT_CARD'
  | 'NET_BANKING'
  | 'WALLET'
  | 'MOCK';

export type PaymentGatewayType = 'MOCK_SANDBOX' | 'RAZORPAY' | 'STRIPE';

export interface CreatePaymentOrderRequest {
  amount: number;
  currency?: string;
  paymentMethod?: PaymentMethod;
  gatewayType?: PaymentGatewayType;
  idempotencyKey?: string;
  customerEmail?: string;
  customerPhone?: string;
  customerName?: string;
  description?: string;
  metadataJson?: string;
}

export interface PaymentOrderDto {
  transactionReference: string;
  gatewayOrderId: string;
  gatewayKeyId?: string;
  clientSecret?: string;
  amount: number;
  currency: string;
  status: PaymentStatus;
  paymentMethod: PaymentMethod;
  gatewayType: PaymentGatewayType;
  upiQrString?: string;
  checkoutUrl?: string;
  createdAt: string;
}

export interface VerifyPaymentRequest {
  transactionReference: string;
  gatewayOrderId?: string;
  gatewayPaymentId?: string;
  gatewaySignature?: string;
  paymentMethod?: PaymentMethod;
  simulateSuccess?: boolean;
  failureReason?: string;
}

export interface PaymentVerificationResultDto {
  transactionReference: string;
  gatewayPaymentId: string;
  status: PaymentStatus;
  amountPaid: number;
  currency: string;
  verified: boolean;
  receiptNumber: string;
  message: string;
  completedAt: string;
}

export interface RefundRequestDto {
  transactionReference: string;
  refundAmount: number;
  reason?: string;
}

export interface RefundResultDto {
  refundReference: string;
  originalTransactionReference: string;
  refundAmount: number;
  status: 'INITIATED' | 'PROCESSED' | 'FAILED';
  gatewayRefundId: string;
  reason?: string;
  processedAt: string;
}

export interface TransactionHistoryDto {
  transactionReference: string;
  gatewayOrderId: string;
  gatewayPaymentId: string;
  amount: number;
  currency: string;
  status: PaymentStatus;
  paymentMethod: PaymentMethod;
  gatewayType: PaymentGatewayType;
  refunds: RefundResultDto[];
  createdAt: string;
  completedAt?: string;
}
