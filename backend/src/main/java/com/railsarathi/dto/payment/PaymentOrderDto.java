package com.railsarathi.dto.payment;

import com.railsarathi.enums.PaymentGatewayType;
import com.railsarathi.enums.PaymentMethod;
import com.railsarathi.enums.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentOrderDto {
    private String transactionReference;
    private String gatewayOrderId;
    private String gatewayKeyId;
    private String clientSecret;
    private BigDecimal amount;
    private String currency;
    private PaymentStatus status;
    private PaymentMethod paymentMethod;
    private PaymentGatewayType gatewayType;
    private String upiQrString;
    private String checkoutUrl;
    private LocalDateTime createdAt;
}
