package com.qt.NotificationService.notification;

import com.qt.NotificationService.event.NotificationEvent;
import lombok.AllArgsConstructor;

public interface INotificationPushingStrategy {
    void push(NotificationEvent notificationEvent);
}
