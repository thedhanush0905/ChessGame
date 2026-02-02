package com.indichess.matchmakingservice.service;

import com.indichess.matchmakingservice.model.MatchQueue;
import com.indichess.matchmakingservice.repository.MatchQueueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@EnableScheduling
@Transactional
public class MatchmakingService {

    private final MatchQueueRepository matchQueueRepository;

    public MatchQueue joinQueue(Long userId, String timeControl, Integer skillLevel) {
        // Check if already in queue
        var existing = matchQueueRepository.findByUserIdAndStatus(userId, MatchQueue.QueueStatus.WAITING);
        if (existing.isPresent()) {
            throw new IllegalArgumentException("User already in queue");
        }

        MatchQueue queue = new MatchQueue();
        queue.setUserId(userId);
        queue.setTimeControl(timeControl);
        queue.setSkillLevel(skillLevel);
        queue.setStatus(MatchQueue.QueueStatus.WAITING);

        MatchQueue saved = matchQueueRepository.save(queue);
        log.info("User {} joined matchmaking queue", userId);
        return saved;
    }

    public void leaveQueue(Long userId) {
        var queueEntry = matchQueueRepository.findByUserIdAndStatus(userId, MatchQueue.QueueStatus.WAITING);
        if (queueEntry.isPresent()) {
            MatchQueue queue = queueEntry.get();
            queue.setStatus(MatchQueue.QueueStatus.CANCELLED);
            matchQueueRepository.save(queue);
            log.info("User {} left matchmaking queue", userId);
        }
    }

    @Scheduled(fixedDelay = 5000) // Run every 5 seconds
    public void matchPlayers() {
        List<MatchQueue> waitingPlayers = matchQueueRepository.findWaitingPlayers();

        // Simple matching algorithm: pair players with similar skill levels
        for (int i = 0; i < waitingPlayers.size() - 1; i++) {
            MatchQueue player1 = waitingPlayers.get(i);
            
            for (int j = i + 1; j < waitingPlayers.size(); j++) {
                MatchQueue player2 = waitingPlayers.get(j);
                
                // Match if same time control and similar skill level
                if (player1.getTimeControl().equals(player2.getTimeControl()) &&
                    Math.abs(player1.getSkillLevel() - player2.getSkillLevel()) <= 200) {
                    
                    matchPlayers(player1, player2);
                    return; // Match found, restart the process
                }
            }
        }
    }

    private void matchPlayers(MatchQueue player1, MatchQueue player2) {
        player1.setStatus(MatchQueue.QueueStatus.MATCHED);
        player1.setMatchedOpponentId(player2.getUserId());
        player1.setMatchedAt(LocalDateTime.now());

        player2.setStatus(MatchQueue.QueueStatus.MATCHED);
        player2.setMatchedOpponentId(player1.getUserId());
        player2.setMatchedAt(LocalDateTime.now());

        matchQueueRepository.saveAll(List.of(player1, player2));
        log.info("Matched players {} and {}", player1.getUserId(), player2.getUserId());
    }

    public MatchQueue getQueueStatus(Long userId) {
        return matchQueueRepository.findByUserIdAndStatus(userId, MatchQueue.QueueStatus.WAITING)
                .orElse(null);
    }
}
