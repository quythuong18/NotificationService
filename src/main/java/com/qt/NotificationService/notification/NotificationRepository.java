package com.qt.NotificationService.notification;

import com.qt.NotificationService.event.NotificationEvent;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface NotificationRepository extends MongoRepository<NotificationMessage, String> {
}
