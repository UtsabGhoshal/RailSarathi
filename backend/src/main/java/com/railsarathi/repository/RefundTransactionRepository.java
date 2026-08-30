package com.railsarathi.repository;

import com.railsarathi.entity.RefundTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RefundTransactionRepository extends JpaRepository<RefundTransaction, Long> {

    Optional<RefundTransaction> findByRefundReference(String refundReference);

    List<RefundTransaction> findByPaymentTransactionId(Long paymentTransactionId);
}
