package com.qt.NotificationService.FCM;

import lombok.Data;

@Data
public class FCMPushNotificationServiceRequest {
    private String fcmToken;
    private String content;
}
