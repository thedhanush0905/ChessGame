package com.indichess.matchmakingservice.repository;

import com.indichess.matchmakingservice.model.MatchQueue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface MatchQueueRepository extends JpaRepository<MatchQueue, Long> {
    Optional<MatchQueue> findByUserIdAndStatus(Long userId, MatchQueue.QueueStatus status);
    
    @Query("SELECT m FROM MatchQueue m WHERE m.status = 'WAITING' ORDER BY m.joinedAt ASC")
    List<MatchQueue> findWaitingPlayers();
    
    @Query("SELECT m FROM MatchQueue m WHERE m.userId = ?1 ORDER BY m.joinedAt DESC")
    List<MatchQueue> findByUserId(Long userId);
}
