package com.railsarathi.service.notification.sender;

import com.railsarathi.dto.NotificationDto;
import com.railsarathi.entity.Notification;
import com.railsarathi.enums.NotificationChannel;
import com.railsarathi.repository.NotificationRepository;
import com.railsarathi.service.notification.NotificationSender;
import com.railsarathi.service.notification.SseConnectionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Persists in-app notifications to PostgreSQL and broadcasts via SSE to active browser sessions.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class InAppNotificationSender implements NotificationSender {

    private final NotificationRepository notificationRepository;
    private final SseConnectionManager sseConnectionManager;

    @Override
    public boolean supports(NotificationChannel channel) {
        return channel == NotificationChannel.IN_APP || channel == NotificationChannel.ALL;
    }

    @Override
    public void send(Notification notification) {
        // Ensure entity is saved in DB
        Notification saved = notification.getId() == null ? notificationRepository.save(notification) : notification;

        if (saved.getRecipientUser() != null) {
            NotificationDto dto = NotificationDto.builder()
                    .id(saved.getId())
                    .recipientUserId(saved.getRecipientUser().getId())
                    .recipientEmail(saved.getRecipientEmail())
                    .title(saved.getTitle())
                    .message(saved.getMessage())
                    .type(saved.getType())
                    .priority(saved.getPriority())
                    .channel(saved.getChannel())
                    .status(saved.getStatus())
                    .actionUrl(saved.getActionUrl())
                    .metadataJson(saved.getMetadataJson())
                    .createdAt(saved.getCreatedAt())
                    .readAt(saved.getReadAt())
                    .build();

            // Push to user's browser via SSE
            sseConnectionManager.sendToUser(saved.getRecipientUser().getId(), dto);
            log.debug("Dispatched in-app notification [{}] to user [{}]", saved.getId(), saved.getRecipientUser().getId());
        }
    }
}
