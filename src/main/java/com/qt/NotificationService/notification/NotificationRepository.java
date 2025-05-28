package com.qt.NotificationService.notification;

import com.qt.NotificationService.event.NotificationEvent;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends MongoRepository<NotificationMessage, String> {
    List<NotificationMessage> findByToUsernameAndIsPushed(String username, Boolean isPushed);
    List<NotificationMessage> findByToUsername(String username, Boolean isPushed);
    Optional<NotificationMessage> findByToUsernameAndNotiMetadataVideoId(String username, String videoId);
    Optional<NotificationMessage> findByToUsernameAndNotiMetadataCommentId(String username, String commentId);
    Slice<NotificationMessage> findByToUsername(String username, Pageable pageable);
}
