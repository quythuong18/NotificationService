package com.qt.NotificationService.notification.strategies;

import com.qt.NotificationService.event.NotificationEvent;
import com.qt.NotificationService.notification.NotificationMessage;
import com.qt.NotificationService.notification.NotificationRepository;
import com.qt.NotificationService.notification.NotificationService;
import com.qt.NotificationService.notification.NotificationTypes;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("LIKE_COMMENT")
@RequiredArgsConstructor
public class LikeCommentNotificationPushing implements INotificationPushingStrategy {
    private final NotificationService notificationService;
    private final NotificationRepository notificationRepository;

    @Override
    public void push(NotificationEvent notificationEvent) {
        if(notificationEvent.getType() != NotificationTypes.LIKE_COMMENT) return;

        String commentId = notificationEvent.getNotiMetadata().getCommentId();
        String fromUsername = notificationEvent.getFromUsername();
        String fromUsernameProfilePic = notificationEvent.getFromUserProfilePic();
        String toUsername = notificationEvent.getToUsernames().get(0);

        NotificationMessage notificationMessage;
        Optional<NotificationMessage> existentNotiMsgOptional =
                notificationRepository.findByToUsernameAndNotiMetadataCommentId(toUsername, commentId);
        if(existentNotiMsgOptional.isPresent()) {
            notificationMessage = existentNotiMsgOptional.get();
            notificationMessage.setMessage(fromUsername + " and others like your comment");
            notificationService.sendNoti(toUsername, notificationMessage);
            return;
        }

        notificationMessage = NotificationMessage.builder()
                .fromUsername(fromUsername)
                .fromUsername(toUsername)
                .fromUserProfilePic(fromUsernameProfilePic)
                .isPushed(Boolean.FALSE)
                .isRead(Boolean.FALSE)
                .type(notificationEvent.getType())
                .message(notificationService.createNotificationMessage(notificationEvent))
                .notiMetadata(notificationEvent.getNotiMetadata())
                .build();
        notificationService.sendNoti(toUsername, notificationMessage);
    }
}
