package com.indichess.gameservice.repository;

import com.indichess.gameservice.model.Game;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
    @Query("SELECT g FROM Game g WHERE g.whitePlayerId = ?1 OR g.blackPlayerId = ?1 ORDER BY g.createdAt DESC")
    Page<Game> findGamesByUserId(Long userId, Pageable pageable);

    @Query("SELECT g FROM Game g WHERE g.status = 'ACTIVE' AND (g.whitePlayerId = ?1 OR g.blackPlayerId = ?1)")
    List<Game> findActiveGamesByUserId(Long userId);

    @Query("SELECT g FROM Game g WHERE g.status = 'PENDING'")
    Page<Game> findPendingGames(Pageable pageable);
}
