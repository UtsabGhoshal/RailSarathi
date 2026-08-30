package com.railsarathi.repository;

import com.railsarathi.entity.PaymentTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {

    Optional<PaymentTransaction> findByTransactionReference(String transactionReference);

    Optional<PaymentTransaction> findByGatewayOrderId(String gatewayOrderId);

    Optional<PaymentTransaction> findByIdempotencyKey(String idempotencyKey);

    Page<PaymentTransaction> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
}
