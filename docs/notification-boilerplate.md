# 📦 Universal Notification Service – Boilerplate & Starter Reference

This document serves as the **architectural guide and reusable code blueprint** for dropping the RailSarathi Notification Service into any Spring Boot + React project.

---

## 🌟 Architectural Features
1. **Pluggable Channel Senders (Open-Closed Principle):** Core dispatcher routes notifications to matching `NotificationSender` implementations (`IN_APP`, `EMAIL`, `SMS`, `WEBHOOK`, `PUSH`).
2. **Extensibility in 1 Step:** To add Telegram, Discord, WhatsApp, or Firebase, create a new `@Component` class implementing `NotificationSender`.
3. **Real-Time Browser Streaming via SSE:** Built-in `SseConnectionManager` supporting heartbeats, auto-cleanup, and user-isolated instant delivery.
4. **Drop-in React UI:** `useNotifications()` hook + `NotificationBell` navbar dropdown + `NotificationToast` floating popups.

---

## 🏗️ 1. Backend Architecture Blueprint

### 1.1 Senders Contract (`NotificationSender.java`)
```java
public interface NotificationSender {
    boolean supports(NotificationChannel channel);
    void send(Notification notification);
}
```

### 1.2 Adding a New Channel (e.g. Discord / WhatsApp / Firebase)
To add a new notification channel to any project, simply implement `NotificationSender`:

```java
@Component
@Slf4j
public class DiscordNotificationSender implements NotificationSender {

    @Override
    public boolean supports(NotificationChannel channel) {
        return channel == NotificationChannel.WEBHOOK;
    }

    @Override
    public void send(Notification notification) {
        // Send webhook payload to Discord channel
        log.info("Sending Discord Webhook: {}", notification.getTitle());
    }
}
```

### 1.3 Asynchronous Dispatching (`NotificationDispatcherService.java`)
```java
@Async
@EventListener
public void onNotificationEvent(NotificationEvent event) {
    for (NotificationSender sender : senders) {
        if (sender.supports(event.getNotification().getChannel())) {
            sender.send(event.getNotification());
        }
    }
}
```

---

## ⚛️ 2. Frontend React Integration Blueprint

### 2.1 Custom Hook (`useNotifications.ts`)
```typescript
import { useNotifications } from '@/hooks/useNotifications';

function AppHeader() {
  const { notifications, unreadCount, markAsRead, markAllAsRead } = useNotifications();

  return (
    <div>
      <span>Unread: {unreadCount}</span>
      <button onClick={markAllAsRead}>Mark all read</button>
    </div>
  );
}
```

### 2.2 Drop-in Notification Bell (`NotificationBell.tsx`)
Just import and render inside your top navigation bar:
```tsx
import { NotificationBell } from '@/components/notifications/NotificationBell';

<nav className="navbar">
  <BrandLogo />
  <NotificationBell />
  <UserProfile />
</nav>
```

---

## 📡 3. REST API Contract

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/v1/notifications` | Paginated in-app notifications (`?unreadOnly=true&page=0&size=20`) |
| `GET` | `/api/v1/notifications/unread-count` | Returns `{ "unreadCount": 3 }` |
| `PUT` | `/api/v1/notifications/{id}/read` | Marks a specific notification as read |
| `PUT` | `/api/v1/notifications/read-all` | Marks all unread notifications as read |
| `DELETE` | `/api/v1/notifications/{id}` | Deletes / archives notification |
| `GET` | `/api/v1/notifications/stream` | **SSE Live Stream** for real-time browser push notifications |
| `POST` | `/api/v1/notifications/send` | Generic dispatch endpoint for service-to-service calls |

---

## 📋 4. How to Copy to a New Project (3 Steps)

1. **Copy Backend Package:** Copy `com.railsarathi.service.notification`, `entity/Notification.java`, `enums/Notification*.java`, and `controller/NotificationController.java`.
2. **Copy Frontend Module:** Copy `src/components/notifications/`, `src/hooks/useNotifications.ts`, `src/api/notificationApi.ts`, and `src/types/notification.types.ts`.
3. **Mount Bell:** Drop `<NotificationBell />` into your layout header!
