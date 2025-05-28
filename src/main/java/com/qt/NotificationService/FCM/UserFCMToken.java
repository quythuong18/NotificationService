package com.qt.NotificationService.FCM;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("user_fcm_tokens")
@Data
public class UserFCMToken {
    private String id;
    private Long userId;
    private String username;
    private List<String> tokens;
}
