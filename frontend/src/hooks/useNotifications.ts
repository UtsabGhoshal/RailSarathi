import { useState, useEffect, useCallback } from 'react';
import { NotificationDto, SendNotificationRequest } from '../types/notification.types';
import { notificationApi } from '../api/notificationApi';
import { useAuth } from '../context/AuthContext';

export function useNotifications() {
  const { user, token, isAuthenticated } = useAuth();
  const [notifications, setNotifications] = useState<NotificationDto[]>([]);
  const [unreadCount, setUnreadCount] = useState<number>(0);
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [activeToast, setActiveToast] = useState<NotificationDto | null>(null);

  const fetchNotifications = useCallback(async () => {
    if (!isAuthenticated) {
      setNotifications([]);
      setUnreadCount(0);
      return;
    }

    setIsLoading(true);
    try {
      const [pageData, count] = await Promise.all([
        notificationApi.getNotifications(false, 0, 30),
        notificationApi.getUnreadCount(),
      ]);
      setNotifications(pageData.content || []);
      setUnreadCount(count);
    } catch (err) {
      console.error('Failed to load notifications:', err);
    } finally {
      setIsLoading(false);
    }
  }, [isAuthenticated]);

  // Initial load on auth change
  useEffect(() => {
    fetchNotifications();
  }, [fetchNotifications]);

  // Connect to live SSE real-time stream
  useEffect(() => {
    if (!isAuthenticated || !token) return;

    const cleanup = notificationApi.createSseStream(
      token,
      (newNotification) => {
        // Optimistically prepend to notification list
        setNotifications((prev) => [newNotification, ...prev]);
        setUnreadCount((prev) => prev + 1);

        // Show toast alert for high/urgent priority items
        if (newNotification.priority === 'HIGH' || newNotification.priority === 'URGENT' || newNotification.type === 'TRAIN_DELAY') {
          setActiveToast(newNotification);
        }
      }
    );

    return () => {
      cleanup();
    };
  }, [isAuthenticated, token]);

  const markAsRead = async (id: number) => {
    try {
      // Optimistic update
      setNotifications((prev) =>
        prev.map((n) => (n.id === id ? { ...n, status: 'READ' as const } : n))
      );
      setUnreadCount((prev) => Math.max(0, prev - 1));

      await notificationApi.markAsRead(id);
    } catch (err) {
      console.error('Failed to mark notification as read:', err);
      fetchNotifications();
    }
  };

  const markAllAsRead = async () => {
    try {
      // Optimistic update
      setNotifications((prev) =>
        prev.map((n) => ({ ...n, status: 'READ' as const }))
      );
      setUnreadCount(0);

      await notificationApi.markAllAsRead();
    } catch (err) {
      console.error('Failed to mark all notifications as read:', err);
      fetchNotifications();
    }
  };

  const deleteNotification = async (id: number) => {
    try {
      const itemToDelete = notifications.find((n) => n.id === id);
      setNotifications((prev) => prev.filter((n) => n.id !== id));
      if (itemToDelete && itemToDelete.status === 'UNREAD') {
        setUnreadCount((prev) => Math.max(0, prev - 1));
      }

      await notificationApi.deleteNotification(id);
    } catch (err) {
      console.error('Failed to delete notification:', err);
      fetchNotifications();
    }
  };

  const sendTestNotification = async (payload: SendNotificationRequest) => {
    return notificationApi.sendNotification(payload);
  };

  const dismissToast = () => {
    setActiveToast(null);
  };

  return {
    notifications,
    unreadCount,
    isLoading,
    activeToast,
    dismissToast,
    markAsRead,
    markAllAsRead,
    deleteNotification,
    sendTestNotification,
    refreshNotifications: fetchNotifications,
  };
}
