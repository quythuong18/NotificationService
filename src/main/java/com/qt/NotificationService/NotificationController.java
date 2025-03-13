package com.qt.NotificationService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class NotificationController {
    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationController.class);

    @MessageMapping("/sendMessage")
    @SendTo("/topic/notifications")
    public NotificationMessage sendNotificationMessage(NotificationMessage message) {
        LOGGER.info("Receive message " + message.getMessage());
        return message;
    }
}
