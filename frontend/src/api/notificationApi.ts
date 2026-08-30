import { request } from './client';
import {
  NotificationDto,
  SendNotificationRequest,
  Page,
} from '../types/notification.types';

export const notificationApi = {
  getNotifications: async (
    unreadOnly: boolean = false,
    page: number = 0,
    size: number = 20
  ): Promise<Page<NotificationDto>> => {
    const params = new URLSearchParams({
      unreadOnly: String(unreadOnly),
      page: String(page),
      size: String(size),
    });
    return request<Page<NotificationDto>>(`/notifications?${params.toString()}`, {
      method: 'GET',
    });
  },

  getUnreadCount: async (): Promise<number> => {
    const res = await request<{ unreadCount: number }>('/notifications/unread-count', {
      method: 'GET',
    });
    return res.unreadCount;
  },

  markAsRead: async (id: number): Promise<NotificationDto> => {
    return request<NotificationDto>(`/notifications/${id}/read`, {
      method: 'PUT',
    });
  },

  markAllAsRead: async (): Promise<number> => {
    const res = await request<{ updatedCount: number }>('/notifications/read-all', {
      method: 'PUT',
    });
    return res.updatedCount;
  },

  deleteNotification: async (id: number): Promise<void> => {
    return request<void>(`/notifications/${id}`, {
      method: 'DELETE',
    });
  },

  sendNotification: async (
    payload: SendNotificationRequest
  ): Promise<NotificationDto> => {
    return request<NotificationDto>('/notifications/send', {
      method: 'POST',
      body: JSON.stringify(payload),
    });
  },

  /**
   * Connects to the backend SSE real-time notification stream with auto-reconnection.
   */
  createSseStream: (
    token: string,
    onNotification: (notification: NotificationDto) => void,
    onAnnouncement?: (announcement: string) => void
  ): (() => void) => {
    const url = `/api/v1/notifications/stream?token=${encodeURIComponent(token)}`;
    const eventSource = new EventSource(url);

    eventSource.addEventListener('NOTIFICATION', (event: MessageEvent) => {
      try {
        const data: NotificationDto = JSON.parse(event.data);
        onNotification(data);
      } catch (err) {
        console.error('Error parsing SSE notification payload:', err);
      }
    });

    if (onAnnouncement) {
      eventSource.addEventListener('ANNOUNCEMENT', (event: MessageEvent) => {
        onAnnouncement(event.data);
      });
    }

    eventSource.onerror = () => {
      // EventSource handles reconnection automatically
    };

    return () => {
      eventSource.close();
    };
  },
};
