package com.railsarathi.service.payment;

import com.railsarathi.dto.payment.*;
import com.railsarathi.entity.PaymentTransaction;
import com.railsarathi.enums.PaymentGatewayType;

/**
 * Common pluggable strategy contract for payment gateway integrations.
 * To integrate a new gateway (e.g. PhonePe, Paytm, PayPal), implement this interface
 * and mark with @Component.
 */
public interface PaymentGatewayProvider {

    PaymentGatewayType getGatewayType();

    PaymentOrderDto createOrder(CreatePaymentOrderRequest request, String transactionReference);

    PaymentVerificationResultDto verifyPayment(VerifyPaymentRequest request, PaymentTransaction transaction);

    RefundResultDto processRefund(RefundRequestDto request, PaymentTransaction transaction);
}
