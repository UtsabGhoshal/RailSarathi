package com.railsarathi.dto.payment;

import com.railsarathi.enums.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerifyPaymentRequest {

    @NotBlank(message = "Transaction reference is required")
    private String transactionReference;

    private String gatewayOrderId;
    private String gatewayPaymentId;
    private String gatewaySignature;

    @Builder.Default
    private PaymentMethod paymentMethod = PaymentMethod.MOCK;

    @Builder.Default
    private boolean simulateSuccess = true;

    private String failureReason;
}
