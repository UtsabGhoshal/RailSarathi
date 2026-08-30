package com.railsarathi.service.notification.sender;

import com.railsarathi.entity.Notification;
import com.railsarathi.enums.NotificationChannel;
import com.railsarathi.service.notification.NotificationSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Handles mobile SMS alert formatting and dispatching.
 */
@Slf4j
@Component
public class SmsNotificationSender implements NotificationSender {

    @Override
    public boolean supports(NotificationChannel channel) {
        return channel == NotificationChannel.SMS || channel == NotificationChannel.ALL;
    }

    @Override
    public void send(Notification notification) {
        String phone = notification.getRecipientPhone();
        if (phone == null && notification.getRecipientUser() != null) {
            phone = notification.getRecipientUser().getPhone();
        }

        if (phone == null || phone.isBlank()) {
            log.warn("Cannot send SMS: recipient phone number is missing for notification [{}]", notification.getTitle());
            return;
        }

        log.info("""
                ============================================================
                📱 [SMS DISPATCHED]
                To:   {}
                Text: [RailSarathi] {}: {}
                ============================================================""",
                phone, notification.getTitle(), notification.getMessage());
    }
}
