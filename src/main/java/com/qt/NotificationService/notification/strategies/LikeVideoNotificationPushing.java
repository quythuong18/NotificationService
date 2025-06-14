package com.qt.NotificationService.notification.strategies;

import com.qt.NotificationService.event.NotificationEvent;
import com.qt.NotificationService.notification.NotificationMessage;
import com.qt.NotificationService.notification.NotificationRepository;
import com.qt.NotificationService.notification.NotificationService;
import com.qt.NotificationService.notification.NotificationTypes;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("LIKE_VIDEO")
@RequiredArgsConstructor
public class LikeVideoNotificationPushing implements INotificationPushingStrategy {
    private final NotificationService notificationService;
    private final NotificationRepository notificationRepository;

    @Override
    public void push(NotificationEvent notificationEvent) {
        if(notificationEvent.getType() != NotificationTypes.LIKE_VIDEO) return;

        String videoId = notificationEvent.getNotiMetadata().getVideoId();
        String fromUsername = notificationEvent.getFromUsername();
        String fromUsernameProfilePic = notificationEvent.getFromUserProfilePic();
        String toUsername = notificationEvent.getToUsernames().get(0);

        NotificationMessage notificationMessage;
        Optional<NotificationMessage> existentNotiMsgOptional =
                notificationRepository.findByToUsernameAndNotiMetadataVideoId(toUsername, videoId);
        if(existentNotiMsgOptional.isPresent()) {
            notificationMessage = existentNotiMsgOptional.get();
            notificationMessage.setMessage(fromUsername + " and others like your video: "
                    + notificationEvent.getNotiMetadata().getVideoTitle());
            notificationService.sendNoti(toUsername, notificationMessage);
            return;
        }

        notificationMessage = NotificationMessage.builder()
                .fromUsername(fromUsername)
                .fromUserProfilePic(fromUsernameProfilePic)
                .toUsername(toUsername)
                .isPushed(Boolean.FALSE)
                .type(notificationEvent.getType())
                .isRead(Boolean.FALSE)
                .message(notificationService.createNotificationMessage(notificationEvent))
                .notiMetadata(notificationEvent.getNotiMetadata())
                .build();
        notificationService.sendNoti(toUsername, notificationMessage);
    }
}
