package com.qt.NotificationService.notification.strategies;

import com.qt.NotificationService.event.NotificationEvent;
import com.qt.NotificationService.notification.NotificationMessage;
import com.qt.NotificationService.notification.NotificationService;
import com.qt.NotificationService.notification.NotificationTypes;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("COMMENT_ON_VIDEO")
@RequiredArgsConstructor
public class CommentNotificationPushing implements INotificationPushingStrategy {
    private final NotificationService notificationService;

    @Override
    public void push(NotificationEvent notificationEvent) {
        if(notificationEvent.getType() != NotificationTypes.COMMENT_ON_VIDEO) return;

        NotificationMessage notificationMessage = NotificationMessage.builder()
                .fromUsername(notificationEvent.getFromUsername())
                .isPushed(Boolean.FALSE)
                .isRead(Boolean.FALSE)
                .type(notificationEvent.getType())
                .message(notificationService.createNotificationMessage(notificationEvent))
                .notiMetadata(notificationEvent.getNotiMetadata())
                .build();
        for(String username : notificationEvent.getToUsernames()) {
            if(username.equals(notificationEvent.getFromUsername())) continue;

            notificationMessage.setToUsername(username);
            notificationService.sendNoti(username, notificationMessage);
        }
    }
}
