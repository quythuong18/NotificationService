package com.qt.NotificationService.notification;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Notification {
    private String fromUsername;
    private List<String> toUsernames;
    private NotificationTypes type;
    private String VideoTitle;
}
