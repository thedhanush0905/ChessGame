package com.indichess.gameservice.dto;

import com.indichess.gameservice.model.Game;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameDTO {
    private Long id;
    private Long whitePlayerId;
    private Long blackPlayerId;
    private String status;
    private String result;
    private Long winnerId;
    private String fen;
    private String pgn;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private String timeControl;
}
