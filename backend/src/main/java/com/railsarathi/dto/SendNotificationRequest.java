package com.railsarathi.dto;

import com.railsarathi.enums.NotificationChannel;
import com.railsarathi.enums.NotificationPriority;
import com.railsarathi.enums.NotificationType;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SendNotificationRequest {
    private Long recipientUserId;
    private String recipientEmail;
    private String recipientPhone;

    @NotBlank(message = "Notification title is required")
    private String title;

    @NotBlank(message = "Notification message is required")
    private String message;

    @Builder.Default
    private NotificationType type = NotificationType.INFO;

    @Builder.Default
    private NotificationPriority priority = NotificationPriority.NORMAL;

    @Builder.Default
    private NotificationChannel channel = NotificationChannel.IN_APP;

    private String actionUrl;
    private String metadataJson;
}
