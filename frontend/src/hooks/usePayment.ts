import { useState } from 'react';
import {
  CreatePaymentOrderRequest,
  PaymentOrderDto,
  VerifyPaymentRequest,
  PaymentVerificationResultDto,
  PaymentMethod,
} from '../types/payment.types';
import { paymentApi } from '../api/paymentApi';

export function usePayment() {
  const [activeOrder, setActiveOrder] = useState<PaymentOrderDto | null>(null);
  const [isModalOpen, setIsModalOpen] = useState<boolean>(false);
  const [isProcessing, setIsProcessing] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);
  const [lastReceipt, setLastReceipt] = useState<PaymentVerificationResultDto | null>(null);

  const initiateCheckout = async (request: CreatePaymentOrderRequest) => {
    setIsProcessing(true);
    setError(null);
    try {
      const order = await paymentApi.createOrder(request);
      setActiveOrder(order);
      setIsModalOpen(true);
      return order;
    } catch (err: unknown) {
      if (err instanceof Error) {
        setError(err.message);
      } else {
        setError('Failed to initiate payment gateway order.');
      }
      throw err;
    } finally {
      setIsProcessing(false);
    }
  };

  const completeVerification = async (
    method: PaymentMethod = 'UPI',
    simulateSuccess: boolean = true
  ): Promise<PaymentVerificationResultDto> => {
    if (!activeOrder) {
      throw new Error('No active payment order found.');
    }

    setIsProcessing(true);
    setError(null);

    try {
      const verifyReq: VerifyPaymentRequest = {
        transactionReference: activeOrder.transactionReference,
        gatewayOrderId: activeOrder.gatewayOrderId,
        paymentMethod: method,
        simulateSuccess,
        gatewayPaymentId: 'pay_' + Math.random().toString(36).substring(2, 10),
      };

      const result = await paymentApi.verifyPayment(verifyReq);
      setLastReceipt(result);
      setIsModalOpen(false);
      return result;
    } catch (err: unknown) {
      if (err instanceof Error) {
        setError(err.message);
      } else {
        setError('Payment verification failed.');
      }
      throw err;
    } finally {
      setIsProcessing(false);
    }
  };

  const closeModal = () => {
    setIsModalOpen(false);
  };

  return {
    activeOrder,
    isModalOpen,
    isProcessing,
    error,
    lastReceipt,
    initiateCheckout,
    completeVerification,
    closeModal,
  };
}
