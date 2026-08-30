import React, { useState, useRef, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Bell,
  Check,
  CheckCheck,
  Trash2,
  Clock,
  CheckCircle2,
  AlertTriangle,
  Sparkles,
  Zap,
  Radio,
} from 'lucide-react';
import { useNotifications } from '../../hooks/useNotifications';
import { NotificationDto, NotificationType } from '../../types/notification.types';
import { NotificationToast } from './NotificationToast';

export const NotificationBell: React.FC = () => {
  const navigate = useNavigate();
  const [isOpen, setIsOpen] = useState<boolean>(false);
  const [activeFilter, setActiveFilter] = useState<'ALL' | 'UNREAD' | 'ALERTS'>('ALL');
  const dropdownRef = useRef<HTMLDivElement>(null);

  const {
    notifications,
    unreadCount,
    activeToast,
    dismissToast,
    markAsRead,
    markAllAsRead,
    deleteNotification,
    sendTestNotification,
  } = useNotifications();

  // Close dropdown when clicking outside
  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target as Node)) {
        setIsOpen(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  const getIcon = (type: NotificationType) => {
    switch (type) {
      case 'BOOKING_CONFIRMATION':
        return <CheckCircle2 size={16} className="text-green" />;
      case 'TRAIN_DELAY':
      case 'PLATFORM_CHANGE':
        return <Clock size={16} className="text-amber" />;
      case 'WARNING':
      case 'SECURITY_ALERT':
        return <AlertTriangle size={16} className="text-rose" />;
      default:
        return <Sparkles size={16} className="text-cyan" />;
    }
  };

  const formatTimeAgo = (dateStr: string) => {
    try {
      const now = new Date();
      const past = new Date(dateStr);
      const diffSec = Math.floor((now.getTime() - past.getTime()) / 1000);

      if (diffSec < 60) return 'Just now';
      if (diffSec < 3600) return `${Math.floor(diffSec / 60)}m ago`;
      if (diffSec < 86400) return `${Math.floor(diffSec / 3600)}h ago`;
      return `${Math.floor(diffSec / 86400)}d ago`;
    } catch {
      return 'Recently';
    }
  };

  const filteredNotifications = notifications.filter((n) => {
    if (activeFilter === 'UNREAD') return n.status === 'UNREAD';
    if (activeFilter === 'ALERTS')
      return (
        n.type === 'TRAIN_DELAY' ||
        n.type === 'PLATFORM_CHANGE' ||
        n.type === 'BOOKING_CONFIRMATION'
      );
    return true;
  });

  const handleNotificationClick = (n: NotificationDto) => {
    if (n.status === 'UNREAD') {
      markAsRead(n.id);
    }
    if (n.actionUrl) {
      setIsOpen(false);
      navigate(n.actionUrl);
    }
  };

  const triggerLiveSimulatedAlert = async () => {
    const randomDelays = [10, 15, 25];
    const delay = randomDelays[Math.floor(Math.random() * randomDelays.length)];

    await sendTestNotification({
      title: `Live Alert: Train 22301 Delay (${delay}m)`,
      message: `TinyFish AI Live Tracker detected a ${delay}-minute signal regulation near Bolpur. Expected arrival: 13:40.`,
      type: 'TRAIN_DELAY',
      priority: 'HIGH',
      channel: 'IN_APP',
      actionUrl: '/train/22301',
    });
  };

  return (
    <div className="notification-center-wrapper" ref={dropdownRef}>
      {/* Bell Trigger Button */}
      <button
        type="button"
        className={`notification-bell-btn ${unreadCount > 0 ? 'has-unread' : ''}`}
        onClick={() => setIsOpen(!isOpen)}
        title="Notifications & Live Updates"
      >
        <Bell size={18} />
        {unreadCount > 0 && (
          <span className="unread-badge-counter">
            {unreadCount > 99 ? '99+' : unreadCount}
          </span>
        )}
      </button>

      {/* Floating Toast Popup */}
      <NotificationToast
        notification={activeToast}
        onDismiss={dismissToast}
        onMarkRead={markAsRead}
      />

      {/* Notification Dropdown Drawer */}
      {isOpen && (
        <div className="notification-dropdown glass">
          {/* Header */}
          <div className="notification-dropdown-header">
            <div className="header-title">
              <h3>Notifications</h3>
              {unreadCount > 0 && (
                <span className="unread-pill">{unreadCount} unread</span>
              )}
            </div>

            <div className="header-actions">
              {unreadCount > 0 && (
                <button
                  type="button"
                  className="btn-text-action"
                  onClick={markAllAsRead}
                  title="Mark all as read"
                >
                  <CheckCheck size={14} /> Mark all read
                </button>
              )}
            </div>
          </div>

          {/* Filter Pills */}
          <div className="notification-filter-pills">
            <button
              type="button"
              className={`pill-tab ${activeFilter === 'ALL' ? 'active' : ''}`}
              onClick={() => setActiveFilter('ALL')}
            >
              All ({notifications.length})
            </button>
            <button
              type="button"
              className={`pill-tab ${activeFilter === 'UNREAD' ? 'active' : ''}`}
              onClick={() => setActiveFilter('UNREAD')}
            >
              Unread ({unreadCount})
            </button>
            <button
              type="button"
              className={`pill-tab ${activeFilter === 'ALERTS' ? 'active' : ''}`}
              onClick={() => setActiveFilter('ALERTS')}
            >
              Trips & Alerts
            </button>
          </div>

          {/* List Area */}
          <div className="notifications-list-container">
            {filteredNotifications.length === 0 ? (
              <div className="notification-empty-state">
                <Bell size={32} className="text-cyan mx-auto mb-2 opacity-50" />
                <p>No {activeFilter.toLowerCase()} notifications.</p>
                <small className="text-muted">You are completely up to date!</small>
              </div>
            ) : (
              filteredNotifications.map((n) => (
                <div
                  key={n.id}
                  className={`notification-item ${n.status === 'UNREAD' ? 'unread' : 'read'}`}
                  onClick={() => handleNotificationClick(n)}
                >
                  <div className="item-icon-box">{getIcon(n.type)}</div>

                  <div className="item-content">
                    <div className="item-header">
                      <strong className="item-title">{n.title}</strong>
                      <span className="item-timestamp">{formatTimeAgo(n.createdAt)}</span>
                    </div>

                    <p className="item-message">{n.message}</p>

                    {n.actionUrl && (
                      <span className="item-action-link">View details →</span>
                    )}
                  </div>

                  <div className="item-actions-hover" onClick={(e) => e.stopPropagation()}>
                    {n.status === 'UNREAD' && (
                      <button
                        type="button"
                        className="item-btn"
                        onClick={() => markAsRead(n.id)}
                        title="Mark as read"
                      >
                        <Check size={13} />
                      </button>
                    )}
                    <button
                      type="button"
                      className="item-btn delete"
                      onClick={() => deleteNotification(n.id)}
                      title="Delete"
                    >
                      <Trash2 size={13} />
                    </button>
                  </div>
                </div>
              ))
            )}
          </div>

          {/* Footer with Live Simulation Trigger */}
          <div className="notification-dropdown-footer">
            <button
              type="button"
              className="simulate-alert-btn"
              onClick={triggerLiveSimulatedAlert}
            >
              <Zap size={13} className="text-cyan" />
              <span>Simulate Real-Time Delay Alert (SSE)</span>
            </button>
          </div>
        </div>
      )}
    </div>
  );
};
