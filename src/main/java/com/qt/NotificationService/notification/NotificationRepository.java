package com.qt.NotificationService.notification;

import com.qt.NotificationService.event.NotificationEvent;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface NotificationRepository extends MongoRepository<NotificationMessage, String> {
    List<NotificationEvent> findByToUsernameAndIsPushed(String username, Boolean isPushed);
    List<NotificationEvent> findByToUsername(String username, Boolean isPushed);
}
