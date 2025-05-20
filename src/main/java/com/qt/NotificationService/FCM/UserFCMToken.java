package com.qt.NotificationService.FCM;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("user_fcm_tokens")
@Data
public class UserFCMToken {
    String id;
    Long userId;
    String username;
    String token;
}
