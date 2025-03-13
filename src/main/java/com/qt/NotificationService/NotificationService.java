package com.qt.NotificationService;

import org.springframework.stereotype.Component;

@Component
public class NotificationService {

    public void sendNotification() {
        // if user is not online just save the message to mongodb
        // if user is online then push the notification
    }

    public Boolean isUserOnline(Long userId) {
        return null;
    }
}
