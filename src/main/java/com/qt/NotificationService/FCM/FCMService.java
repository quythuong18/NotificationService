package com.qt.NotificationService.FCM;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class FCMService {
    private final IUserFCMTokenRepository iUserFCMTokenRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(FCMService.class);

    public String pushNotification(FCMPushNotificationServiceRequest fCMpnsRequest) {
        Message message = Message.builder()
                .putData("content", fCMpnsRequest.getContent())
                .setToken(fCMpnsRequest.getFcmToken())
                .build();

        String response = null;
        try {
            response = FirebaseMessaging.getInstance().send(message);
        } catch (FirebaseMessagingException e) {
            LOGGER.error(e.toString());
        }
        LOGGER.info(response);
        return response;
    }

    public UserFCMToken addNewFCMToken(String token, String username) {
        Optional<UserFCMToken> userFCMTokenOptional = iUserFCMTokenRepository.findByUsername(username);
        if(userFCMTokenOptional.isEmpty()) {
            UserFCMToken newUserFCMToken = new UserFCMToken();
            newUserFCMToken.setUsername(username);

            List<String> tokens = new ArrayList<>();
            tokens.add(token);

            newUserFCMToken.setTokens(tokens);
            return iUserFCMTokenRepository.save(newUserFCMToken);
        }
        UserFCMToken userFCMToken = userFCMTokenOptional.get();
        userFCMToken.getTokens().add(token);
        return iUserFCMTokenRepository.save(userFCMToken);
    }

    public Boolean removeFCMToken(String token, String username) {
        Optional<UserFCMToken> userFCMTokenOptional = iUserFCMTokenRepository.findByUsername(username);
        if(userFCMTokenOptional.isEmpty()) {
            LOGGER.warn("Username not found");
            return Boolean.FALSE;
        }
        UserFCMToken userFCMToken = userFCMTokenOptional.get();
        List<String> tokens = userFCMToken.getTokens();

        Boolean isRemoved = tokens.remove(token);
        // save
        userFCMToken.setTokens(tokens);
        iUserFCMTokenRepository.save(userFCMToken);

        if(!isRemoved) LOGGER.warn("Token not found in the list");

        return isRemoved;
    }
}
