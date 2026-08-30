package com.railsarathi.entity;

import com.railsarathi.enums.NotificationChannel;
import com.railsarathi.enums.NotificationPriority;
import com.railsarathi.enums.NotificationStatus;
import com.railsarathi.enums.NotificationType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications", indexes = {
        @Index(name = "idx_notification_user_status", columnList = "recipient_user_id, status"),
        @Index(name = "idx_notification_created_at", columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_user_id")
    private User recipientUser;

    @Column(name = "recipient_email")
    private String recipientEmail;

    @Column(name = "recipient_phone")
    private String recipientPhone;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    @Builder.Default
    private NotificationType type = NotificationType.INFO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private NotificationPriority priority = NotificationPriority.NORMAL;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private NotificationChannel channel = NotificationChannel.IN_APP;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private NotificationStatus status = NotificationStatus.UNREAD;

    @Column(name = "action_url", length = 500)
    private String actionUrl;

    @Column(name = "metadata_json", columnDefinition = "TEXT")
    private String metadataJson;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "read_at")
    private LocalDateTime readAt;
}
