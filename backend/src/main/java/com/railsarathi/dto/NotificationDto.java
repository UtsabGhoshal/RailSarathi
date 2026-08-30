package com.railsarathi.dto;

import com.railsarathi.enums.NotificationChannel;
import com.railsarathi.enums.NotificationPriority;
import com.railsarathi.enums.NotificationStatus;
import com.railsarathi.enums.NotificationType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDto {
    private Long id;
    private Long recipientUserId;
    private String recipientEmail;
    private String title;
    private String message;
    private NotificationType type;
    private NotificationPriority priority;
    private NotificationChannel channel;
    private NotificationStatus status;
    private String actionUrl;
    private String metadataJson;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;
}
