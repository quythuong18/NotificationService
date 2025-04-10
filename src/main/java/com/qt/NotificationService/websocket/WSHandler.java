package com.qt.NotificationService.websocket;

import com.qt.NotificationService.notification.NotificationRepository;
import com.qt.NotificationService.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class WSHandler extends TextWebSocketHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(WSHandler.class);
    private static final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Lazy
    @Autowired
    private NotificationService notificationService;
    private Object lock = new Object();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String username = (String) session.getAttributes().get("username");
        sessions.put(username, session);
        LOGGER.info("User connected: {}", username);

        // In this step, the user is online again, we will check the DB if there are
        // any notifications to push to this user
        notificationService.resendNotification(username);

    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // I think this is for turn the isRead value is On

        LOGGER.info("Message from {}: {}", (String) session.getAttributes().get("username"), message.getPayload());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String username = (String) session.getAttributes().get("username");
        sessions.remove(username);
        LOGGER.info("User disconnected: {}, Code: {}", (String) session.getAttributes().get("username"), status.getCode());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        LOGGER.warn("Transport error of user: {}, message: {}", (String) session.getAttributes().get("username"),
            exception.getMessage());
    }

    public Boolean sendMessageToUser(String username, TextMessage message) {
        synchronized (lock) { // ensure atomicity
            WebSocketSession session = sessions.get(username);
            if(session != null && session.isOpen()) {
                try {
                    session.sendMessage(message);
                } catch (IOException e) {
                    LOGGER.error(e.getMessage());
                    return false;
                }
                LOGGER.info("Message has been sent to user {}", username);
                sessions.remove(username);
                return true;
            }
        }
        LOGGER.info("User {} is not connected", username);
        return false;
    }

    // check user "online"
    public Boolean isConnected(String username) {
        return sessions.containsKey(username);
    }
}
