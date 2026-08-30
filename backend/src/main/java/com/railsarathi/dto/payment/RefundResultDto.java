package com.railsarathi.dto.payment;

import com.railsarathi.enums.RefundStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefundResultDto {
    private String refundReference;
    private String originalTransactionReference;
    private BigDecimal refundAmount;
    private RefundStatus status;
    private String gatewayRefundId;
    private String reason;
    private LocalDateTime processedAt;
}
