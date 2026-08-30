package com.railsarathi.enums;

/**
 * Status lifecycle of a payment transaction.
 */
public enum PaymentStatus {
    INITIATED,
    PENDING,
    SUCCESS,
    FAILED,
    REFUNDED,
    PARTIALLY_REFUNDED,
    CANCELLED
}
