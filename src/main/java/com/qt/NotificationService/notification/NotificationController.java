package com.qt.NotificationService.notification;

import com.qt.NotificationService.FCM.FCMService;
import com.qt.NotificationService.utils.APIResponse;
import com.qt.NotificationService.utils.APIResponseWithData;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final FCMService fcmService;
    private final NotificationService notificationService;
    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationController.class);

    @GetMapping("/messages")
    public ResponseEntity<APIResponseWithData<List<NotificationMessage>>> getNotificationMessages(
            @RequestHeader("X-Username") String username,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) { return ResponseEntity.ok(new APIResponseWithData<>(Boolean.TRUE, "Get notifications successfully",
                HttpStatus.OK, notificationService.getNotificationMessage(username, page, size)));
    }

    @PostMapping("/token")
    public ResponseEntity<APIResponse> addToken(
            @RequestHeader("X-Username") String username,
            @RequestHeader("X-FCMToken") String token) {
        LOGGER.info(username);
        LOGGER.info(token);
        fcmService.addNewFCMToken(token, username);
        return ResponseEntity.ok(new APIResponse(Boolean.TRUE, "Add token successfully", HttpStatus.OK));
    }

    @DeleteMapping("/token")
    public ResponseEntity<APIResponse> removeToken(
            @RequestHeader("X-Username") String username,
            @RequestHeader("X-FCMToken") String token) {
        if(username == null || username.isBlank() ||
                token == null || token.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new APIResponse(Boolean.FALSE, "Username or token is null or blank",
                            HttpStatus.BAD_REQUEST));
        }

        Boolean isSuccessed = fcmService.removeFCMToken(token, username);

        if(!isSuccessed) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new APIResponse(Boolean.FALSE, "Token does not exist",
                            HttpStatus.BAD_REQUEST));
        }
        return ResponseEntity.ok(new APIResponse(Boolean.TRUE, "Token removed successfully", HttpStatus.OK));
    }
}
