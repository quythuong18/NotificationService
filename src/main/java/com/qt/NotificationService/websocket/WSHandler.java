package com.qt.NotificationService.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WSHandler extends TextWebSocketHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(WSHandler.class);
    private static final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper(); // this is json converter
    private String username;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        username = (String) session.getAttributes().get("username");
        sessions.put(username, session);
        LOGGER.info("User connected: {}", username);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // I think this is for turn the isRead value is On

        LOGGER.info("Message from {}: {}", (String) session.getAttributes().get("username"), message.getPayload());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(username);
        LOGGER.info("User disconnected: {}, Code: {}", (String) session.getAttributes().get("username"), status.getCode());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        LOGGER.warn("Transport error of user: {}, message: {}", (String) session.getAttributes().get("username"),
            exception.getMessage());
    }

    public Boolean sendMessageToUser(String username, WebSocketMessage<?> message) {
        WebSocketSession session = sessions.get(username);
        if(session != null && session.isOpen()) {
            try {
                String jsonMessage = objectMapper.writeValueAsString(message.getPayload());
                session.sendMessage(new TextMessage(jsonMessage));
            } catch (IOException e) {
                LOGGER.error(e.getMessage());
                return false;
            }
            LOGGER.info("Message has been sent to user {}", username);
            return true;
        }
        LOGGER.info("User {} is not connected", username);
        return false;
    }

    // check user "online"
    public Boolean isConnected(String username) {
        return sessions.containsKey(username);
    }
}
