package com.railsarathi.controller;

import com.railsarathi.dto.ApiResponse;
import com.railsarathi.dto.NotificationDto;
import com.railsarathi.dto.SendNotificationRequest;
import com.railsarathi.entity.User;
import com.railsarathi.security.CustomUserDetails;
import com.railsarathi.service.notification.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * Fetch paginated in-app notifications for the authenticated user.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<NotificationDto>>> getUserNotifications(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(name = "unreadOnly", defaultValue = "false") boolean unreadOnly,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<NotificationDto> notifications = notificationService.getUserNotifications(userDetails.getUser(), unreadOnly, pageable);
        return ResponseEntity.ok(ApiResponse.success("Notifications retrieved successfully", notifications));
    }

    /**
     * Get the count of unread notifications for badge indicators.
     */
    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getUnreadCount(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        long count = notificationService.getUnreadCount(userDetails.getUser());
        return ResponseEntity.ok(ApiResponse.success("Unread notification count", Map.of("unreadCount", count)));
    }

    /**
     * Mark a specific notification as read.
     */
    @PutMapping("/{id}/read")
    public ResponseEntity<ApiResponse<NotificationDto>> markAsRead(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id
    ) {
        NotificationDto updated = notificationService.markAsRead(id, userDetails.getUser());
        return ResponseEntity.ok(ApiResponse.success("Notification marked as read", updated));
    }

    /**
     * Mark all unread notifications for the user as read.
     */
    @PutMapping("/read-all")
    public ResponseEntity<ApiResponse<Map<String, Integer>>> markAllAsRead(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        int updatedCount = notificationService.markAllAsRead(userDetails.getUser());
        return ResponseEntity.ok(ApiResponse.success("All notifications marked as read", Map.of("updatedCount", updatedCount)));
    }

    /**
     * Delete/Archive a notification.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteNotification(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id
    ) {
        notificationService.deleteNotification(id, userDetails.getUser());
        return ResponseEntity.ok(ApiResponse.success("Notification deleted successfully", null));
    }

    /**
     * Subscribe to real-time Server-Sent Events (SSE) stream for instant browser updates.
     */
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribeToStream(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return notificationService.subscribeToStream(userDetails.getUser());
    }

    /**
     * Send a notification across any channel. Usable for test triggers or service-to-service calls.
     */
    @PostMapping("/send")
    public ResponseEntity<ApiResponse<NotificationDto>> sendNotification(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody SendNotificationRequest request
    ) {
        // If recipient not explicitly set, default to currently authenticated user
        if (request.getRecipientUserId() == null && userDetails != null) {
            request.setRecipientUserId(userDetails.getUser().getId());
        }

        NotificationDto created = notificationService.send(request);
        return ResponseEntity.ok(ApiResponse.success("Notification dispatched successfully", created));
    }
}
