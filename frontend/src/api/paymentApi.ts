import { request } from './client';
import {
  CreatePaymentOrderRequest,
  PaymentOrderDto,
  VerifyPaymentRequest,
  PaymentVerificationResultDto,
  RefundRequestDto,
  RefundResultDto,
  TransactionHistoryDto,
} from '../types/payment.types';
import { Page } from '../types/notification.types';

export const paymentApi = {
  createOrder: async (payload: CreatePaymentOrderRequest): Promise<PaymentOrderDto> => {
    return request<PaymentOrderDto>('/payments/create-order', {
      method: 'POST',
      body: JSON.stringify(payload),
    });
  },

  verifyPayment: async (
    payload: VerifyPaymentRequest
  ): Promise<PaymentVerificationResultDto> => {
    return request<PaymentVerificationResultDto>('/payments/verify', {
      method: 'POST',
      body: JSON.stringify(payload),
    });
  },

  processRefund: async (payload: RefundRequestDto): Promise<RefundResultDto> => {
    return request<RefundResultDto>('/payments/refund', {
      method: 'POST',
      body: JSON.stringify(payload),
    });
  },

  getTransactionDetails: async (
    transactionRef: string
  ): Promise<TransactionHistoryDto> => {
    return request<TransactionHistoryDto>(`/payments/transactions/${encodeURIComponent(transactionRef)}`, {
      method: 'GET',
    });
  },

  getPaymentHistory: async (
    page: number = 0,
    size: number = 10
  ): Promise<Page<TransactionHistoryDto>> => {
    const params = new URLSearchParams({
      page: String(page),
      size: String(size),
    });
    return request<Page<TransactionHistoryDto>>(`/payments/history?${params.toString()}`, {
      method: 'GET',
    });
  },
};
