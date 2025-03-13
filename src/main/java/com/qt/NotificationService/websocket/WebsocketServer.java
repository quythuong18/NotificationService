package com.qt.NotificationService.websocket;

import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ServerEndpoint("/ws/{username}")
public class WebsocketServer {
    private static final Map<String, Session> userSessions = new ConcurrentHashMap<>();
    private Session session;
    private String username;

    private static final Logger LOGGER = LoggerFactory.getLogger(WebsocketServer.class);

    @OnOpen
    public void onOpen(@PathParam("username") String username, Session session) {
        this.session = session;
        this.username = username;
        userSessions.put(username, session);
        LOGGER.info("User connected: {}", username);
    }

    @OnMessage
    public void onMessage(String message) {
        LOGGER.info("Message from {}: {}", username, message);
    }

    @OnClose
    public void onClose() {
        LOGGER.info("User disconnected:{}", username);
        userSessions.remove(this.username);
    }

    @OnError
    public void onError(Throwable error) {
        LOGGER.error(error.getMessage());
    }
}
