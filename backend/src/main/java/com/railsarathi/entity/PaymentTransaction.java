package com.railsarathi.entity;

import com.railsarathi.enums.PaymentGatewayType;
import com.railsarathi.enums.PaymentMethod;
import com.railsarathi.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "payment_transactions", indexes = {
        @Index(name = "idx_txn_reference", columnList = "transaction_reference", unique = true),
        @Index(name = "idx_txn_user_created", columnList = "user_id, created_at"),
        @Index(name = "idx_txn_idempotency", columnList = "idempotency_key")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "transaction_reference", nullable = false, unique = true, length = 64)
    private String transactionReference;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "gateway_order_id", length = 120)
    private String gatewayOrderId;

    @Column(name = "gateway_payment_id", length = 120)
    private String gatewayPaymentId;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 10)
    @Builder.Default
    private String currency = "INR";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private PaymentStatus status = PaymentStatus.INITIATED;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", length = 30)
    @Builder.Default
    private PaymentMethod paymentMethod = PaymentMethod.MOCK;

    @Enumerated(EnumType.STRING)
    @Column(name = "gateway_type", nullable = false, length = 30)
    @Builder.Default
    private PaymentGatewayType gatewayType = PaymentGatewayType.MOCK_SANDBOX;

    @Column(name = "idempotency_key", length = 120)
    private String idempotencyKey;

    @Column(name = "metadata_json", columnDefinition = "TEXT")
    private String metadataJson;

    @Column(name = "error_message", length = 500)
    private String errorMessage;

    @OneToMany(mappedBy = "paymentTransaction", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<RefundTransaction> refunds = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;
}
