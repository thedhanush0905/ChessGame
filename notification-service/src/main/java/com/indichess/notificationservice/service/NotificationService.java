package com.indichess.notificationservice.service;

import com.indichess.notificationservice.model.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public void sendNotification(Notification notification) {
        String destination = "/topic/notifications/" + notification.getUserId();
        messagingTemplate.convertAndSend(destination, notification);
        log.info("Notification sent to user {} with type {}", notification.getUserId(), notification.getType());
    }

    public void sendGameInvite(Long userId, Long inviterId) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setType("GAME_INVITE");
        notification.setTitle("Game Invite");
        notification.setMessage("Player " + inviterId + " invited you to a game");
        notification.setPayload(inviterId.toString());
        notification.setTimestamp(System.currentTimeMillis());
        
        sendNotification(notification);
    }

    public void sendMatchFound(Long userId, Long opponentId) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setType("MATCH_FOUND");
        notification.setTitle("Match Found!");
        notification.setMessage("We found an opponent for you!");
        notification.setPayload(opponentId.toString());
        notification.setTimestamp(System.currentTimeMillis());
        
        sendNotification(notification);
    }

    public void sendGameStart(Long userId, Long gameId) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setType("GAME_START");
        notification.setTitle("Game Started");
        notification.setMessage("Your game has started!");
        notification.setPayload(gameId.toString());
        notification.setTimestamp(System.currentTimeMillis());
        
        sendNotification(notification);
    }
}
