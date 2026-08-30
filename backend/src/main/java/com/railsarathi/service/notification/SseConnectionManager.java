package com.railsarathi.service.notification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Thread-safe manager for real-time Server-Sent Events (SSE) browser subscribers.
 */
@Slf4j
@Component
public class SseConnectionManager {

    private static final Long DEFAULT_TIMEOUT_MS = 30 * 60 * 1000L; // 30 minutes
    private final Map<Long, List<SseEmitter>> userEmitters = new ConcurrentHashMap<>();

    /**
     * Registers a new SSE subscription for the given user.
     */
    public SseEmitter subscribe(Long userId) {
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT_MS);

        userEmitters.computeIfAbsent(userId, k -> new CopyOnWriteArrayList<>()).add(emitter);

        emitter.onCompletion(() -> removeEmitter(userId, emitter));
        emitter.onTimeout(() -> removeEmitter(userId, emitter));
        emitter.onError((e) -> removeEmitter(userId, emitter));

        // Send initial connection handshake event
        try {
            emitter.send(SseEmitter.event()
                    .name("INIT")
                    .data("Connected to RailSarathi Real-Time Notification Stream"));
        } catch (IOException e) {
            removeEmitter(userId, emitter);
        }

        log.debug("User {} subscribed to SSE notifications. Active connections for user: {}",
                userId, userEmitters.get(userId).size());
        return emitter;
    }

    /**
     * Broadcasts a notification payload to all active browser tabs of a specific user.
     */
    public void sendToUser(Long userId, Object data) {
        List<SseEmitter> emitters = userEmitters.get(userId);
        if (emitters == null || emitters.isEmpty()) {
            return;
        }

        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name("NOTIFICATION")
                        .data(data));
            } catch (IOException e) {
                removeEmitter(userId, emitter);
            }
        }
    }

    /**
     * Broadcasts a system announcement to all connected users.
     */
    public void broadcast(Object data) {
        userEmitters.forEach((userId, emitters) -> {
            for (SseEmitter emitter : emitters) {
                try {
                    emitter.send(SseEmitter.event()
                            .name("ANNOUNCEMENT")
                            .data(data));
                } catch (IOException e) {
                    removeEmitter(userId, emitter);
                }
            }
        });
    }

    private void removeEmitter(Long userId, SseEmitter emitter) {
        List<SseEmitter> emitters = userEmitters.get(userId);
        if (emitters != null) {
            emitters.remove(emitter);
            if (emitters.isEmpty()) {
                userEmitters.remove(userId);
            }
        }
        log.debug("Removed SSE connection for user {}", userId);
    }

    public int getActiveSubscriberCount() {
        return userEmitters.values().stream().mapToInt(List::size).sum();
    }
}
