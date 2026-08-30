package com.railsarathi.service.notification;

import com.railsarathi.entity.Notification;
import com.railsarathi.enums.NotificationChannel;

/**
 * Common pluggable strategy contract for all notification channels.
 * To add a new channel (e.g. WhatsApp, Slack, Firebase), implement this interface
 * and mark with @Component. The dispatcher will auto-discover and route to it.
 */
public interface NotificationSender {

    /**
     * Declares whether this sender handles the specified channel.
     */
    boolean supports(NotificationChannel channel);

    /**
     * Executes asynchronous delivery of the notification.
     */
    void send(Notification notification);
}
