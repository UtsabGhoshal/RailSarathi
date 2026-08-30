package com.railsarathi.dto.payment;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefundRequestDto {

    @NotBlank(message = "Original transaction reference is required")
    private String transactionReference;

    @NotNull(message = "Refund amount is required")
    @DecimalMin(value = "1.00", message = "Minimum refund amount is 1.00")
    private BigDecimal refundAmount;

    @Builder.Default
    private String reason = "Customer requested cancellation";
}
