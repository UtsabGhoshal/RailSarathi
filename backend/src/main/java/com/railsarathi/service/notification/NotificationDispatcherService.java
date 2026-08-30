package com.railsarathi.service.notification;

import com.railsarathi.entity.Notification;
import com.railsarathi.event.NotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Core notification orchestrator. Dispatches notifications to all matching
 * pluggable channel sender strategies asynchronously.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationDispatcherService {

    private final List<NotificationSender> senders;

    /**
     * Handles Spring domain events asynchronously.
     */
    @Async
    @EventListener
    public void onNotificationEvent(NotificationEvent event) {
        dispatch(event.getNotification());
    }

    /**
     * Routes notification to all matching sender strategies.
     */
    public void dispatch(Notification notification) {
        log.info("Dispatching notification [{}] - [{}] via channel [{}]",
                notification.getType(), notification.getTitle(), notification.getChannel());

        for (NotificationSender sender : senders) {
            if (sender.supports(notification.getChannel())) {
                try {
                    sender.send(notification);
                } catch (Exception e) {
                    log.error("Failed to dispatch notification via sender [{}]: {}",
                            sender.getClass().getSimpleName(), e.getMessage(), e);
                }
            }
        }
    }
}
