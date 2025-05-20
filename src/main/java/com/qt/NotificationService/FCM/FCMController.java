package com.qt.NotificationService.FCM;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api/v1/notification")
@RequiredArgsConstructor
public class FCMController {
    private final FCMService fcmService;

    @PostMapping("/register-token")
    public ResponseEntity<UserFCMToken> registerToken(@RequestBody UserFCMToken userFCMToken) {
        if(userFCMToken == null)
            throw new IllegalArgumentException("Body is null");
        return ResponseEntity.ok(fcmService.registerFCMToken(userFCMToken));
    }
    @PostMapping("/update-token")
    public ResponseEntity<UserFCMToken> updateToken(@RequestBody UserFCMToken userFCMToken) {
        if(userFCMToken == null)
            throw new IllegalArgumentException("Body is null");
        return ResponseEntity.ok(fcmService.updateFCMToken(userFCMToken));
    }
}
