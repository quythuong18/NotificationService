package com.qt.NotificationService.FCM;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface IUserFCMTokenRepository extends MongoRepository<UserFCMToken, String> {
    Optional<UserFCMToken> findByUsername(String username);
}
