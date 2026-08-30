package com.railsarathi.service.payment;

import com.railsarathi.dto.payment.*;
import com.railsarathi.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Public service contract for payment checkout, verification, refunds, and transaction history.
 */
public interface PaymentService {

    PaymentOrderDto createOrder(User user, CreatePaymentOrderRequest request);

    PaymentVerificationResultDto verifyPayment(User user, VerifyPaymentRequest request);

    RefundResultDto processRefund(User user, RefundRequestDto request);

    TransactionHistoryDto getTransactionDetails(String transactionReference, User user);

    Page<TransactionHistoryDto> getUserTransactions(User user, Pageable pageable);
}
