package com.railsarathi.dto.payment;

import com.railsarathi.enums.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentVerificationResultDto {
    private String transactionReference;
    private String gatewayPaymentId;
    private PaymentStatus status;
    private BigDecimal amountPaid;
    private String currency;
    private boolean verified;
    private String receiptNumber;
    private String message;
    private LocalDateTime completedAt;
}
