package com.qt.NotificationService.rabbitmq;

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
    private final NotificationService notificationService;

    @RabbitListener(queues = "${rabbitmq.queue.name}")
    public void consumeMessage(String message) {
        LOGGER.info("Received message: {}", message);
    }
}
