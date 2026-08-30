package com.railsarathi.service.notification.impl;

import com.railsarathi.dto.NotificationDto;
import com.railsarathi.dto.SendNotificationRequest;
import com.railsarathi.entity.Notification;
import com.railsarathi.entity.User;
import com.railsarathi.enums.NotificationChannel;
import com.railsarathi.enums.NotificationPriority;
import com.railsarathi.enums.NotificationStatus;
import com.railsarathi.enums.NotificationType;
import com.railsarathi.event.NotificationEvent;
import com.railsarathi.exception.ResourceNotFoundException;
import com.railsarathi.repository.NotificationRepository;
import com.railsarathi.repository.UserRepository;
import com.railsarathi.service.notification.NotificationDispatcherService;
import com.railsarathi.service.notification.NotificationService;
import com.railsarathi.service.notification.SseConnectionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final SseConnectionManager sseConnectionManager;
    private final NotificationDispatcherService notificationDispatcherService;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public NotificationDto send(SendNotificationRequest request) {
        User recipientUser = null;
        if (request.getRecipientUserId() != null) {
            recipientUser = userRepository.findById(request.getRecipientUserId())
                    .orElse(null);
        }

        Notification notification = Notification.builder()
                .recipientUser(recipientUser)
                .recipientEmail(request.getRecipientEmail() != null ? request.getRecipientEmail() : (recipientUser != null ? recipientUser.getEmail() : null))
                .recipientPhone(request.getRecipientPhone() != null ? request.getRecipientPhone() : (recipientUser != null ? recipientUser.getPhone() : null))
                .title(request.getTitle())
                .message(request.getMessage())
                .type(request.getType() != null ? request.getType() : NotificationType.INFO)
                .priority(request.getPriority() != null ? request.getPriority() : NotificationPriority.NORMAL)
                .channel(request.getChannel() != null ? request.getChannel() : NotificationChannel.IN_APP)
                .status(NotificationStatus.UNREAD)
                .actionUrl(request.getActionUrl())
                .metadataJson(request.getMetadataJson())
                .build();

        Notification saved = notificationRepository.save(notification);

        // Dispatch via Spring ApplicationEvent
        eventPublisher.publishEvent(new NotificationEvent(this, saved));

        return mapToDto(saved);
    }

    @Override
    @Transactional
    public NotificationDto sendToUser(User user, NotificationType type, String title, String message, String actionUrl, NotificationChannel channel) {
        SendNotificationRequest request = SendNotificationRequest.builder()
                .recipientUserId(user.getId())
                .recipientEmail(user.getEmail())
                .recipientPhone(user.getPhone())
                .type(type)
                .title(title)
                .message(message)
                .actionUrl(actionUrl)
                .channel(channel != null ? channel : NotificationChannel.IN_APP)
                .priority(NotificationPriority.NORMAL)
                .build();

        return send(request);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationDto> getUserNotifications(User user, boolean unreadOnly, Pageable pageable) {
        Page<Notification> page = unreadOnly
                ? notificationRepository.findByRecipientUserIdAndStatusOrderByCreatedAtDesc(user.getId(), NotificationStatus.UNREAD, pageable)
                : notificationRepository.findByRecipientUserIdOrderByCreatedAtDesc(user.getId(), pageable);

        return page.map(this::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount(User user) {
        return notificationRepository.countByRecipientUserIdAndStatus(user.getId(), NotificationStatus.UNREAD);
    }

    @Override
    @Transactional
    public NotificationDto markAsRead(Long notificationId, User user) {
        Notification notification = notificationRepository.findByIdAndRecipientUserId(notificationId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + notificationId));

        if (notification.getStatus() == NotificationStatus.UNREAD) {
            notification.setStatus(NotificationStatus.READ);
            notification.setReadAt(LocalDateTime.now());
            notification = notificationRepository.save(notification);
        }

        return mapToDto(notification);
    }

    @Override
    @Transactional
    public int markAllAsRead(User user) {
        return notificationRepository.markAllAsReadForUser(user.getId(), LocalDateTime.now());
    }

    @Override
    @Transactional
    public void deleteNotification(Long notificationId, User user) {
        Notification notification = notificationRepository.findByIdAndRecipientUserId(notificationId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + notificationId));

        notificationRepository.delete(notification);
    }

    @Override
    public SseEmitter subscribeToStream(User user) {
        return sseConnectionManager.subscribe(user.getId());
    }

    private NotificationDto mapToDto(Notification entity) {
        return NotificationDto.builder()
                .id(entity.getId())
                .recipientUserId(entity.getRecipientUser() != null ? entity.getRecipientUser().getId() : null)
                .recipientEmail(entity.getRecipientEmail())
                .title(entity.getTitle())
                .message(entity.getMessage())
                .type(entity.getType())
                .priority(entity.getPriority())
                .channel(entity.getChannel())
                .status(entity.getStatus())
                .actionUrl(entity.getActionUrl())
                .metadataJson(entity.getMetadataJson())
                .createdAt(entity.getCreatedAt())
                .readAt(entity.getReadAt())
                .build();
    }
}
