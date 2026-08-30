package com.railsarathi.dto.payment;

import com.railsarathi.enums.PaymentGatewayType;
import com.railsarathi.enums.PaymentMethod;
import com.railsarathi.enums.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionHistoryDto {
    private String transactionReference;
    private String gatewayOrderId;
    private String gatewayPaymentId;
    private BigDecimal amount;
    private String currency;
    private PaymentStatus status;
    private PaymentMethod paymentMethod;
    private PaymentGatewayType gatewayType;
    private List<RefundResultDto> refunds;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
}
