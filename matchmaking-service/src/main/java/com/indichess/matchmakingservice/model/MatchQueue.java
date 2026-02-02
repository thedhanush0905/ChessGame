package com.indichess.matchmakingservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "match_queue")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatchQueue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private QueueStatus status = QueueStatus.WAITING;

    @Column(name = "skill_level")
    private Integer skillLevel;

    @Column(name = "time_control")
    private String timeControl = "rapid";

    @Column(name = "joined_at")
    private LocalDateTime joinedAt;

    @Column(name = "matched_opponent_id")
    private Long matchedOpponentId;

    @Column(name = "matched_at")
    private LocalDateTime matchedAt;

    @Column(name = "game_id")
    private Long gameId;

    @PrePersist
    protected void onCreate() {
        joinedAt = LocalDateTime.now();
    }

    public enum QueueStatus {
        WAITING, MATCHED, CANCELLED, COMPLETED
    }
}
