package com.qt.NotificationService.notification;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qt.NotificationService.event.NotificationEvent;
import com.qt.NotificationService.websocket.WSHandler;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;

import java.util.List;
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

    public void pushNotification(NotificationEvent notificationEvent) {
        List<String> usernamelist = new CopyOnWriteArrayList<>(notificationEvent.getToUsernames());
        ExecutorService executor = Executors.newFixedThreadPool(12);

        // create the message for the notification for each user
        String notificationMsg = createNotificationMessage(notificationEvent);
        /*
            {
              "fromUsername": "qt",
              "toUsernames": ["quythuong18"],
              "type": "NEW_VIDEO",
              "metadata": {
                "videoId": "123",
                "videoTitle": "Me in US"
              }
            }
        * */
        for(String username : usernamelist) {
            executor.submit(() -> {
                NotificationMessage notificationMessage = NotificationMessage.builder()
                        .fromUsername(notificationEvent.getFromUsername())
                        .toUsername(username)
                        .type(notificationEvent.getType())
                        .metadata(notificationEvent.getMetadata())
                        .isPushed(Boolean.FALSE)
                        .isRead(Boolean.FALSE)
                        .message(createNotificationMessage(notificationEvent))
                        .build();
                String jsonMessage = null;
                // map to json
                try {
                    jsonMessage = objectMapper.writeValueAsString(notificationMessage);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }

                if(wsHandler.isConnected(username) &&
                        wsHandler.sendMessageToUser(username, new TextMessage(jsonMessage))) {
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
                    notificationEvent.getMetadata().getVideoTitle();
            case COMMENT_ON_VIDEO -> message = notificationEvent.getFromUsername() + " commented on your video: " +
                    notificationEvent.getMetadata().getComment();
            case LIKE_VIDEO -> message = notificationEvent.getFromUsername() + " liked your video" +
                    notificationEvent.getMetadata().getVideoTitle();
            case LIKE_COMMENT -> message = notificationEvent.getFromUsername() + " liked your comment" +
                    notificationEvent.getMetadata().getComment();
        }

        return message;
    }
}
