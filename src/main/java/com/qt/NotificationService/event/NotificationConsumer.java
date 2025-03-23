package com.qt.NotificationService.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qt.NotificationService.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationConsumer.class);
    private final ObjectMapper objectMapper; // this is for json converter
    private final NotificationService notificationService;

    @RabbitListener(queues = {"${rabbitmq.queue.name}"})
    public void consumeMessage(String jsonNotificationPayLoad) throws JsonProcessingException {
        NotificationEvent event = objectMapper.readValue(jsonNotificationPayLoad, NotificationEvent.class);
        LOGGER.info("Received message: {}", event.toString());
    }
}
