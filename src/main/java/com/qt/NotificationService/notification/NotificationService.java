package com.qt.NotificationService.notification;

import com.qt.NotificationService.websocket.WSHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Component
@RequiredArgsConstructor
public class NotificationService {
    private final WSHandler wsHandler;

    public void pushNotification(Notification noti) {
        // if user is not online just save the message to mongodb
        // if user is online then push the notification

        List<String> usernames = new CopyOnWriteArrayList<>(noti.getToUsernames());
        ExecutorService executor = Executors.newFixedThreadPool(5);

        for(String u : usernames) {
            executor.submit(() -> {
                // some works
            });
        }
    }
}
