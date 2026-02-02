package com.indichess.gameservice.service;

import com.indichess.gameservice.dto.GameDTO;
import com.indichess.gameservice.model.Game;
import com.indichess.gameservice.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class GameService {

    private final GameRepository gameRepository;

    public GameDTO createGame(Long whitePlayerId, Long blackPlayerId) {
        Game game = new Game();
        game.setWhitePlayerId(whitePlayerId);
        game.setBlackPlayerId(blackPlayerId);
        game.setStatus(Game.GameStatus.PENDING);

        Game saved = gameRepository.save(game);
        log.info("Game created: {} (White: {}, Black: {})", saved.getId(), whitePlayerId, blackPlayerId);
        return mapToDTO(saved);
    }

    public GameDTO getGame(Long gameId) {
        return gameRepository.findById(gameId)
                .map(this::mapToDTO)
                .orElseThrow(() -> new RuntimeException("Game not found"));
    }

    public GameDTO startGame(Long gameId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Game not found"));

        game.setStatus(Game.GameStatus.ACTIVE);
        game.setStartedAt(LocalDateTime.now());

        Game updated = gameRepository.save(game);
        return mapToDTO(updated);
    }

    public GameDTO updateGameState(Long gameId, String fen, String pgn) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Game not found"));

        game.setFen(fen);
        game.setPgn(pgn);

        Game updated = gameRepository.save(game);
        return mapToDTO(updated);
    }

    public GameDTO endGame(Long gameId, Game.GameResult result, Long winnerId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Game not found"));

        game.setStatus(Game.GameStatus.COMPLETED);
        game.setResult(result);
        game.setWinnerId(winnerId);
        game.setEndedAt(LocalDateTime.now());

        Game updated = gameRepository.save(game);
        log.info("Game ended: {} with result: {}", gameId, result);
        return mapToDTO(updated);
    }

    public Page<GameDTO> getUserGameHistory(Long userId, Pageable pageable) {
        return gameRepository.findGamesByUserId(userId, pageable)
                .map(this::mapToDTO);
    }

    public Page<GameDTO> getPendingGames(Pageable pageable) {
        return gameRepository.findPendingGames(pageable)
                .map(this::mapToDTO);
    }

    private GameDTO mapToDTO(Game game) {
        return new GameDTO(
                game.getId(),
                game.getWhitePlayerId(),
                game.getBlackPlayerId(),
                game.getStatus().name(),
                game.getResult() != null ? game.getResult().name() : null,
                game.getWinnerId(),
                game.getFen(),
                game.getPgn(),
                game.getCreatedAt(),
                game.getUpdatedAt(),
                game.getStartedAt(),
                game.getEndedAt(),
                game.getTimeControl()
        );
    }
}
