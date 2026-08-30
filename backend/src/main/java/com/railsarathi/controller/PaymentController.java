package com.railsarathi.controller;

import com.railsarathi.dto.ApiResponse;
import com.railsarathi.dto.payment.*;
import com.railsarathi.security.CustomUserDetails;
import com.railsarathi.service.payment.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * Creates a new payment order across the requested gateway provider.
     */
    @PostMapping("/create-order")
    public ResponseEntity<ApiResponse<PaymentOrderDto>> createOrder(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CreatePaymentOrderRequest request
    ) {
        PaymentOrderDto order = paymentService.createOrder(
                userDetails != null ? userDetails.getUser() : null,
                request
        );
        return ResponseEntity.ok(ApiResponse.success("Payment order created successfully", order));
    }

    /**
     * Verifies payment completion and authorizes the transaction.
     */
    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<PaymentVerificationResultDto>> verifyPayment(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody VerifyPaymentRequest request
    ) {
        PaymentVerificationResultDto result = paymentService.verifyPayment(
                userDetails != null ? userDetails.getUser() : null,
                request
        );
        return ResponseEntity.ok(ApiResponse.success("Payment verification processed", result));
    }

    /**
     * Executes an instant full or partial refund.
     */
    @PostMapping("/refund")
    public ResponseEntity<ApiResponse<RefundResultDto>> processRefund(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody RefundRequestDto request
    ) {
        RefundResultDto refundResult = paymentService.processRefund(
                userDetails != null ? userDetails.getUser() : null,
                request
        );
        return ResponseEntity.ok(ApiResponse.success("Refund processed successfully", refundResult));
    }

    /**
     * Fetches detailed transaction status and audit ledger.
     */
    @GetMapping("/transactions/{transactionRef}")
    public ResponseEntity<ApiResponse<TransactionHistoryDto>> getTransactionDetails(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String transactionRef
    ) {
        TransactionHistoryDto details = paymentService.getTransactionDetails(
                transactionRef,
                userDetails != null ? userDetails.getUser() : null
        );
        return ResponseEntity.ok(ApiResponse.success("Transaction details retrieved", details));
    }

    /**
     * Retrieves paginated payment history for the authenticated user.
     */
    @GetMapping("/history")
    public ResponseEntity<ApiResponse<Page<TransactionHistoryDto>>> getUserPaymentHistory(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<TransactionHistoryDto> history = paymentService.getUserTransactions(userDetails.getUser(), pageable);
        return ResponseEntity.ok(ApiResponse.success("Payment history retrieved successfully", history));
    }

    /**
     * Generic webhook receiver for asynchronous gateway callbacks.
     */
    @PostMapping("/webhook/{provider}")
    public ResponseEntity<Map<String, Object>> handleGatewayWebhook(
            @PathVariable String provider,
            @RequestBody(required = false) String payload,
            @RequestHeader Map<String, String> headers
    ) {
        log.info("[PAYMENT WEBHOOK] Received async callback from provider [{}], payload length [{}]",
                provider, payload != null ? payload.length() : 0);
        return ResponseEntity.ok(Map.of("status", "received", "provider", provider));
    }
}
