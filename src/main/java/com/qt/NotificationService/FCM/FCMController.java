package com.qt.NotificationService.FCM;

import com.qt.NotificationService.security.JWTService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController("/api/v1/notification")
@RequiredArgsConstructor
public class FCMController {
    private final FCMService fcmService;
    private final JWTService jwtService;

    @PostMapping("/token")
    public ResponseEntity<?> registerToken(
            @RequestHeader("Authorization") String bearerToken,
            @RequestBody UserFCMToken userFCMToken) {
        if(userFCMToken == null)
            throw new IllegalArgumentException("Body is null");
        if(!extractAndValidateBearerToken(bearerToken, userFCMToken.getUsername()))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                 .body("Authentication failed: Invalid token");
        return ResponseEntity.ok(fcmService.registerFCMToken(userFCMToken));
    }
    @PutMapping("/token")
    public ResponseEntity<?> updateToken(
            @RequestHeader("Authorization") String bearerToken,
            @RequestBody UserFCMToken userFCMToken
    ) {
        if(userFCMToken == null)
            throw new IllegalArgumentException("Body is null");
        if(!extractAndValidateBearerToken(bearerToken, userFCMToken.getUsername()))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Authentication failed: Invalid token");
        return ResponseEntity.ok(fcmService.updateFCMToken(userFCMToken));
    }

    public Boolean extractAndValidateBearerToken(String bearerToken, String username) {
        if(bearerToken == null || !bearerToken.startsWith("Bearer ")) return Boolean.FALSE;
        String token = bearerToken.substring(7);
        return jwtService.isValid(token, username);
    }
}
