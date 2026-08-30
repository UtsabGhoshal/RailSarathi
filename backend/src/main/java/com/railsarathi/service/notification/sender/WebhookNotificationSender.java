package com.railsarathi.service.notification.sender;

import com.railsarathi.entity.Notification;
import com.railsarathi.enums.NotificationChannel;
import com.railsarathi.service.notification.NotificationSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Handles generic external webhook / API callbacks.
 */
@Slf4j
@Component
public class WebhookNotificationSender implements NotificationSender {

    @Override
    public boolean supports(NotificationChannel channel) {
        return channel == NotificationChannel.WEBHOOK || channel == NotificationChannel.ALL;
    }

    @Override
    public void send(Notification notification) {
        log.debug("Dispatched Webhook payload for notification [{}] type [{}]", notification.getId(), notification.getType());
    }
}
