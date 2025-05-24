package com.qt.NotificationService.notification.strategies;

import com.qt.NotificationService.event.NotificationEvent;
import com.qt.NotificationService.notification.NotificationMessage;
import com.qt.NotificationService.notification.NotificationService;
import com.qt.NotificationService.notification.NotificationTypes;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("FOLLOW")
@RequiredArgsConstructor
public class FollowNotificationPushing implements INotificationPushingStrategy {
    private final NotificationService notificationService;

    @Override
    public void push(NotificationEvent notificationEvent) {
        if(notificationEvent.getType() != NotificationTypes.FOLLOW) return;

        NotificationMessage notificationMessage = NotificationMessage.builder()
                .fromUsername(notificationEvent.getFromUsername())
                .toUsername(notificationEvent.getToUsernames().get(0)) // following feature just send to 1 user
                .isPushed(Boolean.FALSE)
                .isRead(Boolean.FALSE)
                .type(notificationEvent.getType())
                .message(notificationService.createNotificationMessage(notificationEvent))
                .notiMetadata(notificationEvent.getNotiMetadata())
                .build();
        notificationService.sendNoti(notificationEvent.getToUsernames().get(0), notificationMessage);
    }
}
