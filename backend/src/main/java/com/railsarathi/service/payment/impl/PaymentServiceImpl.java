package com.railsarathi.service.payment.impl;

import com.railsarathi.dto.payment.*;
import com.railsarathi.entity.PaymentTransaction;
import com.railsarathi.entity.RefundTransaction;
import com.railsarathi.entity.User;
import com.railsarathi.enums.*;
import com.railsarathi.exception.BadRequestException;
import com.railsarathi.exception.ResourceNotFoundException;
import com.railsarathi.repository.PaymentTransactionRepository;
import com.railsarathi.repository.RefundTransactionRepository;
import com.railsarathi.service.notification.NotificationService;
import com.railsarathi.service.payment.PaymentGatewayFactory;
import com.railsarathi.service.payment.PaymentGatewayProvider;
import com.railsarathi.service.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentTransactionRepository transactionRepository;
    private final RefundTransactionRepository refundRepository;
    private final PaymentGatewayFactory gatewayFactory;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public PaymentOrderDto createOrder(User user, CreatePaymentOrderRequest request) {
        // 1. Idempotency Check: Avoid double order creation
        if (request.getIdempotencyKey() != null && !request.getIdempotencyKey().isBlank()) {
            Optional<PaymentTransaction> existing = transactionRepository.findByIdempotencyKey(request.getIdempotencyKey());
            if (existing.isPresent()) {
                PaymentTransaction txn = existing.get();
                log.info("Idempotent hit: Returning existing transaction [{}]", txn.getTransactionReference());
                return mapToOrderDto(txn);
            }
        }

        // 2. Generate unique transaction reference
        String datePrefix = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomSuffix = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String txnRef = "TXN-" + datePrefix + "-" + randomSuffix;

        // 3. Delegate to gateway provider
        PaymentGatewayProvider provider = gatewayFactory.getProvider(request.getGatewayType());
        PaymentOrderDto orderDto = provider.createOrder(request, txnRef);

        // 4. Save transaction ledger
        PaymentTransaction transaction = PaymentTransaction.builder()
                .transactionReference(txnRef)
                .user(user)
                .gatewayOrderId(orderDto.getGatewayOrderId())
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .status(PaymentStatus.INITIATED)
                .paymentMethod(request.getPaymentMethod())
                .gatewayType(request.getGatewayType())
                .idempotencyKey(request.getIdempotencyKey())
                .metadataJson(request.getMetadataJson())
                .build();

        transactionRepository.save(transaction);
        log.info("Created payment transaction ledger entry [{}] amount [{} {}]", txnRef, request.getAmount(), request.getCurrency());

        return orderDto;
    }

    @Override
    @Transactional
    public PaymentVerificationResultDto verifyPayment(User user, VerifyPaymentRequest request) {
        PaymentTransaction transaction = transactionRepository.findByTransactionReference(request.getTransactionReference())
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found for reference: " + request.getTransactionReference()));

        if (transaction.getStatus() == PaymentStatus.SUCCESS) {
            log.info("Transaction [{}] already verified as SUCCESS", transaction.getTransactionReference());
            return PaymentVerificationResultDto.builder()
                    .transactionReference(transaction.getTransactionReference())
                    .gatewayPaymentId(transaction.getGatewayPaymentId())
                    .status(PaymentStatus.SUCCESS)
                    .amountPaid(transaction.getAmount())
                    .currency(transaction.getCurrency())
                    .verified(true)
                    .message("Payment was already verified successfully.")
                    .completedAt(transaction.getCompletedAt())
                    .build();
        }

        PaymentGatewayProvider provider = gatewayFactory.getProvider(transaction.getGatewayType());
        PaymentVerificationResultDto result = provider.verifyPayment(request, transaction);

        if (result.isVerified() && result.getStatus() == PaymentStatus.SUCCESS) {
            transaction.setStatus(PaymentStatus.SUCCESS);
            transaction.setGatewayPaymentId(result.getGatewayPaymentId());
            transaction.setCompletedAt(LocalDateTime.now());
            if (request.getPaymentMethod() != null) {
                transaction.setPaymentMethod(request.getPaymentMethod());
            }
            transactionRepository.save(transaction);

            // Trigger In-App notification
            if (transaction.getUser() != null) {
                notificationService.sendToUser(
                        transaction.getUser(),
                        NotificationType.SUCCESS,
                        "Payment Successful (" + transaction.getCurrency() + " " + transaction.getAmount() + ")",
                        "Transaction " + transaction.getTransactionReference() + " authorized successfully via " + transaction.getPaymentMethod() + ".",
                        "/dashboard",
                        NotificationChannel.IN_APP
                );
            }
        } else {
            transaction.setStatus(PaymentStatus.FAILED);
            transaction.setErrorMessage(result.getMessage());
            transactionRepository.save(transaction);
        }

        return result;
    }

    @Override
    @Transactional
    public RefundResultDto processRefund(User user, RefundRequestDto request) {
        PaymentTransaction transaction = transactionRepository.findByTransactionReference(request.getTransactionReference())
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found for reference: " + request.getTransactionReference()));

        if (transaction.getStatus() != PaymentStatus.SUCCESS && transaction.getStatus() != PaymentStatus.PARTIALLY_REFUNDED) {
            throw new BadRequestException("Cannot refund transaction in status: " + transaction.getStatus());
        }

        // Calculate already refunded total
        BigDecimal alreadyRefunded = transaction.getRefunds().stream()
                .filter(r -> r.getStatus() == RefundStatus.PROCESSED)
                .map(RefundTransaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal availableToRefund = transaction.getAmount().subtract(alreadyRefunded);
        if (request.getRefundAmount().compareTo(availableToRefund) > 0) {
            throw new BadRequestException("Requested refund amount (" + request.getRefundAmount() + ") exceeds available balance (" + availableToRefund + ")");
        }

        PaymentGatewayProvider provider = gatewayFactory.getProvider(transaction.getGatewayType());
        RefundResultDto refundResult = provider.processRefund(request, transaction);

        // Save refund ledger
        RefundTransaction refundEntity = RefundTransaction.builder()
                .refundReference(refundResult.getRefundReference())
                .paymentTransaction(transaction)
                .amount(request.getRefundAmount())
                .reason(request.getReason())
                .status(refundResult.getStatus())
                .gatewayRefundId(refundResult.getGatewayRefundId())
                .processedAt(refundResult.getProcessedAt())
                .build();

        refundRepository.save(refundEntity);
        transaction.getRefunds().add(refundEntity);

        // Update transaction status
        BigDecimal newTotalRefunded = alreadyRefunded.add(request.getRefundAmount());
        if (newTotalRefunded.compareTo(transaction.getAmount()) >= 0) {
            transaction.setStatus(PaymentStatus.REFUNDED);
        } else {
            transaction.setStatus(PaymentStatus.PARTIALLY_REFUNDED);
        }
        transactionRepository.save(transaction);

        // Emit instant refund notification
        if (transaction.getUser() != null) {
            notificationService.sendToUser(
                    transaction.getUser(),
                    NotificationType.BOOKING_CANCELLATION,
                    "Refund Processed (" + transaction.getCurrency() + " " + request.getRefundAmount() + ")",
                    "Refund reference " + refundResult.getRefundReference() + " has been initiated to your original payment source.",
                    "/dashboard",
                    NotificationChannel.IN_APP
            );
        }

        return refundResult;
    }

    @Override
    @Transactional(readOnly = true)
    public TransactionHistoryDto getTransactionDetails(String transactionReference, User user) {
        PaymentTransaction txn = transactionRepository.findByTransactionReference(transactionReference)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found: " + transactionReference));

        if (user != null && txn.getUser() != null && !txn.getUser().getId().equals(user.getId()) && !user.getRole().name().equals("ROLE_ADMIN")) {
            throw new BadRequestException("Access denied to transaction: " + transactionReference);
        }

        return mapToHistoryDto(txn);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TransactionHistoryDto> getUserTransactions(User user, Pageable pageable) {
        return transactionRepository.findByUserIdOrderByCreatedAtDesc(user.getId(), pageable)
                .map(this::mapToHistoryDto);
    }

    private PaymentOrderDto mapToOrderDto(PaymentTransaction txn) {
        return PaymentOrderDto.builder()
                .transactionReference(txn.getTransactionReference())
                .gatewayOrderId(txn.getGatewayOrderId())
                .amount(txn.getAmount())
                .currency(txn.getCurrency())
                .status(txn.getStatus())
                .paymentMethod(txn.getPaymentMethod())
                .gatewayType(txn.getGatewayType())
                .createdAt(txn.getCreatedAt())
                .build();
    }

    private TransactionHistoryDto mapToHistoryDto(PaymentTransaction txn) {
        List<RefundResultDto> refunds = txn.getRefunds().stream()
                .map(r -> RefundResultDto.builder()
                        .refundReference(r.getRefundReference())
                        .originalTransactionReference(txn.getTransactionReference())
                        .refundAmount(r.getAmount())
                        .status(r.getStatus())
                        .gatewayRefundId(r.getGatewayRefundId())
                        .reason(r.getReason())
                        .processedAt(r.getProcessedAt())
                        .build())
                .collect(Collectors.toList());

        return TransactionHistoryDto.builder()
                .transactionReference(txn.getTransactionReference())
                .gatewayOrderId(txn.getGatewayOrderId())
                .gatewayPaymentId(txn.getGatewayPaymentId())
                .amount(txn.getAmount())
                .currency(txn.getCurrency())
                .status(txn.getStatus())
                .paymentMethod(txn.getPaymentMethod())
                .gatewayType(txn.getGatewayType())
                .refunds(refunds)
                .createdAt(txn.getCreatedAt())
                .completedAt(txn.getCompletedAt())
                .build();
    }
}
