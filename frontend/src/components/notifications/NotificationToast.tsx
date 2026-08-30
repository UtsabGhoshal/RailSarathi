import React, { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { X, Bell, CheckCircle2, AlertTriangle, Clock, Sparkles } from 'lucide-react';
import { NotificationDto, NotificationType } from '../../types/notification.types';

interface NotificationToastProps {
  notification: NotificationDto | null;
  onDismiss: () => void;
  onMarkRead?: (id: number) => void;
}

export const NotificationToast: React.FC<NotificationToastProps> = ({
  notification,
  onDismiss,
  onMarkRead,
}) => {
  const navigate = useNavigate();

  useEffect(() => {
    if (!notification) return;

    const timer = setTimeout(() => {
      onDismiss();
    }, 6500);

    return () => clearTimeout(timer);
  }, [notification, onDismiss]);

  if (!notification) return null;

  const getIcon = (type: NotificationType) => {
    switch (type) {
      case 'BOOKING_CONFIRMATION':
        return <CheckCircle2 size={20} className="text-green" />;
      case 'TRAIN_DELAY':
        return <Clock size={20} className="text-amber" />;
      case 'WARNING':
      case 'SECURITY_ALERT':
        return <AlertTriangle size={20} className="text-rose" />;
      default:
        return <Sparkles size={20} className="text-cyan" />;
    }
  };

  const handleAction = () => {
    if (onMarkRead) {
      onMarkRead(notification.id);
    }
    onDismiss();
    if (notification.actionUrl) {
      navigate(notification.actionUrl);
    }
  };

  return (
    <div className="notification-toast-floating glass" role="alert">
      <div className="toast-icon-box">{getIcon(notification.type)}</div>

      <div className="toast-body" onClick={handleAction}>
        <div className="toast-title-row">
          <strong className="toast-title">{notification.title}</strong>
          <span className="toast-time">Just now</span>
        </div>
        <p className="toast-message">{notification.message}</p>
        {notification.actionUrl && (
          <span className="toast-action-link">Open details →</span>
        )}
      </div>

      <button
        type="button"
        className="toast-close-btn"
        onClick={(e) => {
          e.stopPropagation();
          onDismiss();
        }}
        title="Dismiss alert"
      >
        <X size={14} />
      </button>
    </div>
  );
};
