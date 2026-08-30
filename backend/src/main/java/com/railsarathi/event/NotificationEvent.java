package com.railsarathi.event;

import com.railsarathi.entity.Notification;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Domain event dispatched when a new notification is generated.
 */
@Getter
public class NotificationEvent extends ApplicationEvent {

    private final Notification notification;

    public NotificationEvent(Object source, Notification notification) {
        super(source);
        this.notification = notification;
    }
}
