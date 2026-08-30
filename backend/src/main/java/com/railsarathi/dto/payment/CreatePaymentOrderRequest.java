package com.railsarathi.dto.payment;

import com.railsarathi.enums.PaymentGatewayType;
import com.railsarathi.enums.PaymentMethod;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatePaymentOrderRequest {

    @NotNull(message = "Payment amount is required")
    @DecimalMin(value = "1.00", message = "Minimum transaction amount is 1.00")
    private BigDecimal amount;

    @Builder.Default
    private String currency = "INR";

    @Builder.Default
    private PaymentMethod paymentMethod = PaymentMethod.MOCK;

    @Builder.Default
    private PaymentGatewayType gatewayType = PaymentGatewayType.MOCK_SANDBOX;

    private String idempotencyKey;
    private String customerEmail;
    private String customerPhone;
    private String customerName;
    private String description;
    private String metadataJson;
}
