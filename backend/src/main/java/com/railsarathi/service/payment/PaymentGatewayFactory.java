package com.railsarathi.service.payment;

import com.railsarathi.enums.PaymentGatewayType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Factory for resolving active PaymentGatewayProvider strategies.
 */
@Slf4j
@Component
public class PaymentGatewayFactory {

    private final Map<PaymentGatewayType, PaymentGatewayProvider> providerMap = new EnumMap<>(PaymentGatewayType.class);

    public PaymentGatewayFactory(List<PaymentGatewayProvider> providers) {
        for (PaymentGatewayProvider provider : providers) {
            providerMap.put(provider.getGatewayType(), provider);
            log.info("Registered Payment Gateway Provider: [{}]", provider.getGatewayType());
        }
    }

    public PaymentGatewayProvider getProvider(PaymentGatewayType type) {
        if (type == null) {
            type = PaymentGatewayType.MOCK_SANDBOX;
        }
        PaymentGatewayProvider provider = providerMap.get(type);
        if (provider == null) {
            log.warn("Payment provider [{}] not found. Falling back to MOCK_SANDBOX.", type);
            return providerMap.get(PaymentGatewayType.MOCK_SANDBOX);
        }
        return provider;
    }
}
