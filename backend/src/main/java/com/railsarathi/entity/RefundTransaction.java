package com.railsarathi.entity;

import com.railsarathi.enums.RefundStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "refund_transactions", indexes = {
        @Index(name = "idx_refund_reference", columnList = "refund_reference", unique = true),
        @Index(name = "idx_refund_txn_id", columnList = "payment_transaction_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefundTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "refund_reference", nullable = false, unique = true, length = 64)
    private String refundReference;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_transaction_id", nullable = false)
    private PaymentTransaction paymentTransaction;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(length = 250)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private RefundStatus status = RefundStatus.INITIATED;

    @Column(name = "gateway_refund_id", length = 120)
    private String gatewayRefundId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;
}
