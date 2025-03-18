package com.qt.NotificationService.event;

import com.qt.NotificationService.notification.NotificationTypes;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class NotificationEvent {
    private String fromUsername;
    private List<String> toUsernames;
    private NotificationTypes type;
    private String VideoTitle;
}
