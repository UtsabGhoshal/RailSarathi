package com.railsarathi.service.payment.provider;

import com.railsarathi.dto.payment.*;
import com.railsarathi.entity.PaymentTransaction;
import com.railsarathi.enums.PaymentGatewayType;
import com.railsarathi.enums.PaymentStatus;
import com.railsarathi.enums.RefundStatus;
import com.railsarathi.service.payment.PaymentGatewayProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * High-fidelity zero-credential developer sandbox for testing payment and refund flows.
 */
@Slf4j
@Component
public class MockSandboxPaymentProvider implements PaymentGatewayProvider {

    @Override
    public PaymentGatewayType getGatewayType() {
        return PaymentGatewayType.MOCK_SANDBOX;
    }

    @Override
    public PaymentOrderDto createOrder(CreatePaymentOrderRequest request, String transactionReference) {
        String mockOrderId = "order_mock_" + UUID.randomUUID().toString().substring(0, 8);
        String upiQr = String.format("upi://pay?pa=railsarathi@irctc&pn=RailSarathi&am=%.2f&tr=%s&tn=RailSarathi+Ticket",
                request.getAmount(), transactionReference);

        log.info("[SANDBOX GATEWAY] Created mock order [{}] for reference [{}] amount [{} {}]",
                mockOrderId, transactionReference, request.getAmount(), request.getCurrency());

        return PaymentOrderDto.builder()
                .transactionReference(transactionReference)
                .gatewayOrderId(mockOrderId)
                .gatewayKeyId("rzp_test_mock_sandbox_key")
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .status(PaymentStatus.INITIATED)
                .paymentMethod(request.getPaymentMethod())
                .gatewayType(PaymentGatewayType.MOCK_SANDBOX)
                .upiQrString(upiQr)
                .checkoutUrl("/checkout/mock/" + transactionReference)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Override
    public PaymentVerificationResultDto verifyPayment(VerifyPaymentRequest request, PaymentTransaction transaction) {
        if (!request.isSimulateSuccess()) {
            log.warn("[SANDBOX GATEWAY] Simulating failed payment for transaction [{}]", transaction.getTransactionReference());
            return PaymentVerificationResultDto.builder()
                    .transactionReference(transaction.getTransactionReference())
                    .status(PaymentStatus.FAILED)
                    .verified(false)
                    .message(request.getFailureReason() != null ? request.getFailureReason() : "Payment rejected by mock bank gateway simulator.")
                    .completedAt(LocalDateTime.now())
                    .build();
        }

        String paymentId = "pay_mock_" + UUID.randomUUID().toString().substring(0, 10);
        String receiptNumber = "RCP-RS-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        log.info("[SANDBOX GATEWAY] Verified mock payment [{}] for reference [{}] amount [{} {}]",
                paymentId, transaction.getTransactionReference(), transaction.getAmount(), transaction.getCurrency());

        return PaymentVerificationResultDto.builder()
                .transactionReference(transaction.getTransactionReference())
                .gatewayPaymentId(paymentId)
                .status(PaymentStatus.SUCCESS)
                .amountPaid(transaction.getAmount())
                .currency(transaction.getCurrency())
                .verified(true)
                .receiptNumber(receiptNumber)
                .message("Payment successfully authorized via RailSarathi Sandbox.")
                .completedAt(LocalDateTime.now())
                .build();
    }

    @Override
    public RefundResultDto processRefund(RefundRequestDto request, PaymentTransaction transaction) {
        String refundRef = "RFD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String gatewayRefundId = "rfd_mock_" + UUID.randomUUID().toString().substring(0, 10);

        log.info("[SANDBOX GATEWAY] Processed instant mock refund [{}] for transaction [{}] amount [{}]",
                gatewayRefundId, transaction.getTransactionReference(), request.getRefundAmount());

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
