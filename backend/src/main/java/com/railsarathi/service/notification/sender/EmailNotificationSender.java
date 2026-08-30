package com.railsarathi.service.notification.sender;

import com.railsarathi.entity.Notification;
import com.railsarathi.enums.NotificationChannel;
import com.railsarathi.service.notification.NotificationSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Handles HTML email template generation and dispatch.
 * In development, logs a clean simulated email card to the console.
 */
@Slf4j
@Component
public class EmailNotificationSender implements NotificationSender {

    @Override
    public boolean supports(NotificationChannel channel) {
        return channel == NotificationChannel.EMAIL || channel == NotificationChannel.ALL;
    }

    @Override
    public void send(Notification notification) {
        String recipient = notification.getRecipientEmail();
        if (recipient == null && notification.getRecipientUser() != null) {
            recipient = notification.getRecipientUser().getEmail();
        }

        if (recipient == null || recipient.isBlank()) {
            log.warn("Cannot send email notification: recipient email is missing for notification [{}]", notification.getTitle());
            return;
        }

        log.info("""
                ============================================================
                📧 [EMAIL DISPATCHED]
                To:      {}
                Subject: [RailSarathi] {}
                Type:    {} | Priority: {}
                Body:
                {}
                ============================================================""",
                recipient, notification.getTitle(), notification.getType(), notification.getPriority(), notification.getMessage());
    }
}
