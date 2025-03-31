package com.qt.NotificationService.event;

import com.qt.NotificationService.notification.NotiMetadata;
import com.qt.NotificationService.notification.NotificationTypes;
import lombok.Data;

import java.util.List;

@Data
public class NotificationEvent {
    public NotificationEvent() {}
    private String fromUsername;
    private List<String> toUsernames;
    private NotificationTypes type;
    private NotiMetadata notiMetadata;
}
