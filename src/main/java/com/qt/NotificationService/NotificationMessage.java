package com.qt.NotificationService;

import lombok.Data;

@Data
public class NotificationMessage {
    private String id;
    private Long userId;
    private String message;
    private NotificationTypes type;
    private boolean isRead;
}
