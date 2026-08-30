package com.railsarathi.service.notification;

import com.railsarathi.dto.NotificationDto;
import com.railsarathi.dto.SendNotificationRequest;
import com.railsarathi.entity.User;
import com.railsarathi.enums.NotificationChannel;
import com.railsarathi.enums.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * Public service interface for notification querying, streaming, and dispatching.
 */
public interface NotificationService {

    NotificationDto send(SendNotificationRequest request);

    NotificationDto sendToUser(User user, NotificationType type, String title, String message, String actionUrl, NotificationChannel channel);

    Page<NotificationDto> getUserNotifications(User user, boolean unreadOnly, Pageable pageable);

    long getUnreadCount(User user);

    NotificationDto markAsRead(Long notificationId, User user);

    int markAllAsRead(User user);

    void deleteNotification(Long notificationId, User user);

    SseEmitter subscribeToStream(User user);
}
