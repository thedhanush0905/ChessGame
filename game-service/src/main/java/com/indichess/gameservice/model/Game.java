package com.indichess.gameservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "games")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "white_player_id", nullable = false)
    private Long whitePlayerId;

    @Column(name = "black_player_id", nullable = false)
    private Long blackPlayerId;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private GameStatus status = GameStatus.PENDING;

    @Column(name = "result")
    @Enumerated(EnumType.STRING)
    private GameResult result;

    @Column(name = "winner_id")
    private Long winnerId;

    @Column(name = "fen")
    private String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    @Column(name = "pgn", columnDefinition = "TEXT")
    private String pgn = "";

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "ended_at")
    private LocalDateTime endedAt;

    @Column(name = "time_control")
    private String timeControl = "unlimited";

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum GameStatus {
        PENDING, ACTIVE, COMPLETED, ABANDONED
    }

    public enum GameResult {
        WHITE_WIN, BLACK_WIN, DRAW, ABANDONED
    }
}
