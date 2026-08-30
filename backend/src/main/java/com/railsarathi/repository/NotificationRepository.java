package com.railsarathi.repository;

import com.railsarathi.entity.Notification;
import com.railsarathi.enums.NotificationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findByRecipientUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    Page<Notification> findByRecipientUserIdAndStatusOrderByCreatedAtDesc(Long userId, NotificationStatus status, Pageable pageable);

    long countByRecipientUserIdAndStatus(Long userId, NotificationStatus status);

    Optional<Notification> findByIdAndRecipientUserId(Long id, Long userId);

    @Modifying
    @Query("UPDATE Notification n SET n.status = 'READ', n.readAt = :now WHERE n.recipientUser.id = :userId AND n.status = 'UNREAD'")
    int markAllAsReadForUser(@Param("userId") Long userId, @Param("now") LocalDateTime now);
}
