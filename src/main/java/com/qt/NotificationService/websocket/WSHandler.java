package com.qt.NotificationService.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class WSHandler extends TextWebSocketHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(WSHandler.class);
    private static final CopyOnWriteArrayList<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        LOGGER.info("User connected: {}", (String) session.getAttributes().get("username"));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // I think this for turn the isRead value is On

        LOGGER.info("Message from {}: {}", (String) session.getAttributes().get("username"), message.getPayload());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        LOGGER.info("User disconnected: {}, Code: {}", (String) session.getAttributes().get("username"), status.getCode());
    }


}
