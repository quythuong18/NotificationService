package com.qt.NotificationService.FCM;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class FCMService {
    private final IUserFCMTokenRepository iUserFCMTokenRepository;

    public String pushNotification(FCMPushNotificationServiceRequest fCMpnsRequest) {
        Message message = Message.builder()
                .putData("content", fCMpnsRequest.getContent())
                .setToken(fCMpnsRequest.getFcmToken())
                .build();

        String response = null;
        try {
            response = FirebaseMessaging.getInstance().send(message);
        } catch (FirebaseMessagingException e) {
            e.printStackTrace();
        }
        return response;
    }

    public UserFCMToken registerFCMToken(UserFCMToken userFCMToken) {
        return iUserFCMTokenRepository.save(userFCMToken);
    }

    public UserFCMToken updateFCMToken(UserFCMToken userFCMToken) {
        Optional<UserFCMToken> userFCMTokenOptional = iUserFCMTokenRepository.findByUsername(userFCMToken.getUsername());
        if(userFCMTokenOptional.isEmpty())
            throw new IllegalArgumentException("Username in FCM Tokens db not found");
        UserFCMToken newUserFCMToken = userFCMTokenOptional.get();
        newUserFCMToken.setToken(userFCMToken.getToken());
        return iUserFCMTokenRepository.save(newUserFCMToken);
    }
}
