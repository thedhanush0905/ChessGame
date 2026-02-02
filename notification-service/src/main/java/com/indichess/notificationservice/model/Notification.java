package com.indichess.notificationservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    private Long userId;
    private String type; // GAME_INVITE, MATCH_FOUND, GAME_START, GAME_END, CHAT_MESSAGE
    private String title;
    private String message;
    private String payload;
    private Long timestamp;
}
