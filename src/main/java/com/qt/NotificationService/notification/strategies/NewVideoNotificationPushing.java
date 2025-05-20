package com.qt.NotificationService.notification.strategies;

import com.qt.NotificationService.event.NotificationEvent;
import com.qt.NotificationService.notification.NotificationMessage;
import com.qt.NotificationService.notification.NotificationService;
import com.qt.NotificationService.notification.NotificationTypes;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@AllArgsConstructor
@Component("NEW_VIDEO")
public class NewVideoNotificationPushing implements INotificationPushingStrategy {
    private final NotificationService notificationService;
    @Override
    public void push(NotificationEvent notificationEvent) {
        if(notificationEvent.getType() != NotificationTypes.NEW_VIDEO) return;

        // send msg to the video owner user
        NotificationMessage notificationMsgForVideoOwner = NotificationMessage.builder()
                .fromUsername(notificationEvent.getFromUsername())
                .toUsername(notificationEvent.getFromUsername()) // owner
                .isPushed(Boolean.FALSE)
                .isRead(Boolean.FALSE)
                .message("Your video uploaded successfully")
                .build();
        notificationService.sendNoti(notificationEvent.getFromUsername(), notificationMsgForVideoOwner);

        // create the message for the notification for each follower users
        String notificationStringMsg = notificationService.createNotificationMessage(notificationEvent);

        List<String> usernamelist = new CopyOnWriteArrayList<>(notificationEvent.getToUsernames());

        NotificationMessage notificationMessage = NotificationMessage.builder()
                .fromUsername(notificationEvent.getFromUsername())
                .type(notificationEvent.getType())
                .notiMetadata(notificationEvent.getNotiMetadata())
                .isPushed(Boolean.FALSE)
                .isRead(Boolean.FALSE)
                .message(notificationStringMsg)
                .build();
        ExecutorService executor = Executors.newFixedThreadPool(12);
        for(String username : usernamelist) {
            executor.submit(() -> {
                 notificationMessage.setToUsername(username);

                notificationService.sendNoti(username, notificationMessage);
            });
        }
    }
}
