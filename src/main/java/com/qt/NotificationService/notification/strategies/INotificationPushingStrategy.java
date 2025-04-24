package com.qt.NotificationService.notification.strategies;

import com.qt.NotificationService.event.NotificationEvent;

public interface INotificationPushingStrategy {
    void push(NotificationEvent notificationEvent);
}
