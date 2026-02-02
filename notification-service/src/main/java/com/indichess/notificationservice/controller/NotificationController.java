package com.indichess.notificationservice.controller;

import com.indichess.notificationservice.model.Notification;
import com.indichess.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @MessageMapping("/notify/{userId}")
    @SendTo("/topic/notifications/{userId}")
    public Notification notifyUser(Notification notification, @DestinationVariable Long userId) {
        notification.setUserId(userId);
        notification.setTimestamp(System.currentTimeMillis());
        notificationService.sendNotification(notification);
        return notification;
    }

    @PostMapping("/api/notifications/send")
    public ResponseEntity<?> sendNotification(@RequestBody Notification notification) {
        try {
            notificationService.sendNotification(notification);
            return ResponseEntity.ok("Notification sent");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/api/notifications/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Notification Service is running");
    }
}
