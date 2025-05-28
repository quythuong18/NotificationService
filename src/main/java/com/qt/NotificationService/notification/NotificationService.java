package com.qt.NotificationService.notification;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qt.NotificationService.FCM.FCMPushNotificationServiceRequest;
import com.qt.NotificationService.FCM.FCMService;
import com.qt.NotificationService.FCM.IUserFCMTokenRepository;
import com.qt.NotificationService.FCM.UserFCMToken;
import com.qt.NotificationService.event.NotificationEvent;
import com.qt.NotificationService.websocket.WSHandler;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Component
@RequiredArgsConstructor
public class NotificationService {
    private final WSHandler wsHandler;
    private final FCMService fcmService;
    private final IUserFCMTokenRepository iUserFCMTokenRepository;
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
                notificationRepository.save(noti);
            }
        }
    }

    public void sendNoti(String username, NotificationMessage notiMsg) {
        sendToUserByFCM(username, notiMsg);
        sendToWSEndpoint(username, notiMsg);
    }

    public void sendToWSEndpoint(String username, NotificationMessage notiMsg) {
        if(wsHandler.isConnected(username) && wsHandler.sendMessageToUser(username,
                new TextMessage(notificationMessagetoJsonString(notiMsg)))) {
            LOGGER.info("Notification pushed to {}", username);
            notiMsg.setIsPushed(Boolean.TRUE);
        }
        else {
            LOGGER.info("Notification was not pushed because {} is not online", username);
        }
        notificationRepository.save(notiMsg);
    }

    public void sendToUserByFCM(String username, NotificationMessage notiMsg) {
        String content;
        try {
            content = objectMapper.writeValueAsString(notiMsg);
        } catch (JsonProcessingException e) { throw new RuntimeException(e); }

        Optional<UserFCMToken> userFCMTokenOptional = iUserFCMTokenRepository.findByUsername(username);

        if(userFCMTokenOptional.isEmpty()) {
            LOGGER.warn("{} token does not exist", username);
            return;
        }

        List<String> tokens = userFCMTokenOptional.get().getTokens();
//        for(String t : tokens) {
//            request.setFcmToken(t);
//            request.setContent(content);
//            fcmService.pushNotification(request);
//        }
        ExecutorService executor = Executors.newFixedThreadPool(4);
        for(String t : tokens) {
            executor.submit(() -> {
                FCMPushNotificationServiceRequest request = new FCMPushNotificationServiceRequest();
                request.setFcmToken(t);
                request.setContent(content);
                fcmService.pushNotification(request);
            });
        }
    }

    public List<NotificationMessage> getNotificationMessage(String username, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Slice<NotificationMessage> pageNotifcationMsgs = notificationRepository
                .findByToUsername(username, pageable);

        return pageNotifcationMsgs.getContent();
    }

    public String createNotificationMessage(NotificationEvent notificationEvent) {
        String message = "";
        switch (notificationEvent.getType()) {
            case FOLLOW -> message = notificationEvent.getFromUsername() + " starts following you";
            case NEW_VIDEO -> message = notificationEvent.getFromUsername() + " uploaded a new video: " +
                    notificationEvent.getNotiMetadata().getVideoTitle();
            case COMMENT_ON_VIDEO -> message = notificationEvent.getFromUsername() + " commented on your video: " +
                    notificationEvent.getNotiMetadata().getComment();
            case LIKE_VIDEO -> message = notificationEvent.getFromUsername() + " liked your video: " +
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
