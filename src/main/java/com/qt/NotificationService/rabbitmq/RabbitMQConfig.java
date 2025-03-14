package com.qt.NotificationService.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    @Value("$rabbitmq.exchange.name")
    private String exchangeName;

    @Value("$rabbitmq.queue.name")
    private String queueName;

    @Value("$rabbitmq.routing.key")
    private String routingKey;

    public Queue queue() {
        return new Queue(queueName);
    }
    public TopicExchange exchange() {
        return new TopicExchange("");
    }

    public Binding notificationBinding() {
        return BindingBuilder
                .bind(queue())
                .to(exchange())
                .with(routingKey);
    }
}
