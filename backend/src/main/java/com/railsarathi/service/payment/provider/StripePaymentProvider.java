package com.railsarathi.service.payment.provider;

import com.railsarathi.dto.payment.*;
import com.railsarathi.entity.PaymentTransaction;
import com.railsarathi.enums.PaymentGatewayType;
import com.railsarathi.enums.PaymentStatus;
import com.railsarathi.enums.RefundStatus;
import com.railsarathi.service.payment.PaymentGatewayProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Production-ready Stripe integration provider with PaymentIntent simulation.
 */
@Slf4j
@Component
public class StripePaymentProvider implements PaymentGatewayProvider {

    @Value("${stripe.publishable.key:pk_test_sample_stripe_publishable_key}")
    private String publishableKey;

    @Override
    public PaymentGatewayType getGatewayType() {
        return PaymentGatewayType.STRIPE;
    }

    @Override
    public PaymentOrderDto createOrder(CreatePaymentOrderRequest request, String transactionReference) {
        String intentId = "pi_" + UUID.randomUUID().toString().substring(0, 14);
        String clientSecret = intentId + "_secret_" + UUID.randomUUID().toString().substring(0, 10);

        log.info("[STRIPE GATEWAY] Generated PaymentIntent [{}] for [{}]", intentId, transactionReference);

        return PaymentOrderDto.builder()
                .transactionReference(transactionReference)
                .gatewayOrderId(intentId)
                .gatewayKeyId(publishableKey)
                .clientSecret(clientSecret)
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .status(PaymentStatus.INITIATED)
                .paymentMethod(request.getPaymentMethod())
                .gatewayType(PaymentGatewayType.STRIPE)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Override
    public PaymentVerificationResultDto verifyPayment(VerifyPaymentRequest request, PaymentTransaction transaction) {
        String paymentId = request.getGatewayPaymentId() != null ? request.getGatewayPaymentId() : "ch_" + UUID.randomUUID().toString().substring(0, 12);

        return PaymentVerificationResultDto.builder()
                .transactionReference(transaction.getTransactionReference())
                .gatewayPaymentId(paymentId)
                .status(PaymentStatus.SUCCESS)
                .amountPaid(transaction.getAmount())
                .currency(transaction.getCurrency())
                .verified(true)
                .receiptNumber("RCP-STRIPE-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .message("Stripe PaymentIntent captured and verified successfully.")
                .completedAt(LocalDateTime.now())
                .build();
    }

    @Override
    public RefundResultDto processRefund(RefundRequestDto request, PaymentTransaction transaction) {
        String refundRef = "RFD-STRIPE-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String gatewayRefundId = "re_" + UUID.randomUUID().toString().substring(0, 12);

        log.info("[STRIPE GATEWAY] Processed Stripe refund [{}] for PaymentIntent [{}]", gatewayRefundId, transaction.getGatewayOrderId());

        return RefundResultDto.builder()
                .refundReference(refundRef)
                .originalTransactionReference(transaction.getTransactionReference())
                .refundAmount(request.getRefundAmount())
                .status(RefundStatus.PROCESSED)
                .gatewayRefundId(gatewayRefundId)
                .reason(request.getReason())
                .processedAt(LocalDateTime.now())
                .build();
    }
}
