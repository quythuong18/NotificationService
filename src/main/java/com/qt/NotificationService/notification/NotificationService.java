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

        for(String username : usernamelist) {
            executor.submit(() -> {
                NotificationMessage notificationMessage = NotificationMessage.builder()
                        .fromUsername(notificationEvent.getFromUsername())
                        .toUsername(username)
                        .type(notificationEvent.getType())
                        .isPushed(Boolean.FALSE)
                        .isRead(Boolean.FALSE)
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
}
