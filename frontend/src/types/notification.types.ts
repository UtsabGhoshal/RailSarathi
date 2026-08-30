export type NotificationType =
  | 'INFO'
  | 'SUCCESS'
  | 'WARNING'
  | 'ERROR'
  | 'BOOKING_CONFIRMATION'
  | 'BOOKING_CANCELLATION'
  | 'TRAIN_DELAY'
  | 'PLATFORM_CHANGE'
  | 'SCHEDULE_UPDATE'
  | 'SECURITY_ALERT'
  | 'SYSTEM_ANNOUNCEMENT';

export type NotificationPriority = 'LOW' | 'NORMAL' | 'HIGH' | 'URGENT';
export type NotificationChannel = 'IN_APP' | 'EMAIL' | 'SMS' | 'WEBHOOK' | 'PUSH' | 'ALL';
export type NotificationStatus = 'UNREAD' | 'READ' | 'ARCHIVED';

export interface NotificationDto {
  id: number;
  recipientUserId?: number;
  recipientEmail?: string;
  title: string;
  message: string;
  type: NotificationType;
  priority: NotificationPriority;
  channel: NotificationChannel;
  status: NotificationStatus;
  actionUrl?: string;
  metadataJson?: string;
  createdAt: string;
  readAt?: string;
}

export interface SendNotificationRequest {
  recipientUserId?: number;
  recipientEmail?: string;
  recipientPhone?: string;
  title: string;
  message: string;
  type?: NotificationType;
  priority?: NotificationPriority;
  channel?: NotificationChannel;
  actionUrl?: string;
  metadataJson?: string;
}

export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
  empty: boolean;
}
