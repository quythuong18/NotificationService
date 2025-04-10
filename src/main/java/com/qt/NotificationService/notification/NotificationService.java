package com.qt.NotificationService.notification;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qt.NotificationService.event.NotificationEvent;
import com.qt.NotificationService.websocket.WSHandler;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Component
@RequiredArgsConstructor
public class NotificationService {
    private final WSHandler wsHandler;
    private final NotificationRepository notificationRepository;
    private final ObjectMapper objectMapper; // this is for json converter
    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationService.class);

    @Async
    public void resendNotification(String username) {
        List<NotificationMessage> notificationList =
            notificationRepository.findByToUsernameAndIsPushed(username, Boolean.FALSE);

        // prioritize sending noti to user
        for(NotificationMessage noti : notificationList) {
            if(wsHandler.sendMessageToUser(username, new TextMessage(notificationMessagetoJsonString(noti)))) {
                noti.setIsPushed(Boolean.TRUE);
            }
        }

        for(NotificationMessage noti : notificationList) {
            if(noti.getIsPushed() == Boolean.TRUE)
                notificationRepository.save(noti);
        }
    }

    public void pushNotification(NotificationEvent notificationEvent) {
        List<String> usernamelist = new CopyOnWriteArrayList<>(notificationEvent.getToUsernames());
        ExecutorService executor = Executors.newFixedThreadPool(12);

        // create the message for the notification for each user
        String notificationMsg = createNotificationMessage(notificationEvent);
        /*
            {
              "fromUsername": "qt",
              "toUsernames": ["quythuong"],
              "type": "NEW_VIDEO",
              "metadata": {
                "videoId": "123",
                "videoTitle": "Me in the US"
             }
        * */
        for(String username : usernamelist) {
            executor.submit(() -> {
                NotificationMessage notificationMessage = NotificationMessage.builder()
                        .fromUsername(notificationEvent.getFromUsername())
                        .toUsername(username)
                        .type(notificationEvent.getType())
                        .notiMetadata(notificationEvent.getNotiMetadata())
                        .isPushed(Boolean.FALSE)
                        .isRead(Boolean.FALSE)
                        .message(createNotificationMessage(notificationEvent))
                        .build();

                if(wsHandler.isConnected(username) && wsHandler.sendMessageToUser(username,
                                new TextMessage(notificationMessagetoJsonString(notificationMessage)))) {
                    LOGGER.info("Notification pushed to {}", username);
                    notificationMessage.setIsPushed(Boolean.TRUE);
                }
                else {
                    LOGGER.info("Notification was not pushed because {} is not online or some errors occur", username);
                }
                notificationRepository.save(notificationMessage);
            });
        }
    }

    public String createNotificationMessage(NotificationEvent notificationEvent) {
        String message = "";
        switch (notificationEvent.getType()) {
            case FOLLOW -> message = notificationEvent.getFromUsername() + " starts following you";
            case NEW_VIDEO -> message = notificationEvent.getFromUsername() + " uploaded a new video: " +
                    notificationEvent.getNotiMetadata().getVideoTitle();
            case COMMENT_ON_VIDEO -> message = notificationEvent.getFromUsername() + " commented on your video: " +
                    notificationEvent.getNotiMetadata().getComment();
            case LIKE_VIDEO -> message = notificationEvent.getFromUsername() + " liked your video" +
                    notificationEvent.getNotiMetadata().getVideoTitle();
            case LIKE_COMMENT -> message = notificationEvent.getFromUsername() + " liked your comment" +
                    notificationEvent.getNotiMetadata().getComment();
        }

        return message;
    }
    public String notificationMessagetoJsonString(NotificationMessage notiMsg) {
        String jsonMessage = null;
        // map to json
        try {
            jsonMessage = objectMapper.writeValueAsString(notiMsg);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return jsonMessage;
    }
}
