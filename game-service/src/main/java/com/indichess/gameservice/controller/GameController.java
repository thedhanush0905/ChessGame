package com.indichess.gameservice.controller;

import com.indichess.gameservice.dto.GameDTO;
import com.indichess.gameservice.model.Game;
import com.indichess.gameservice.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/games")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class GameController {

    private final GameService gameService;

    @PostMapping
    public ResponseEntity<GameDTO> createGame(
            @RequestParam Long whitePlayerId,
            @RequestParam Long blackPlayerId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(gameService.createGame(whitePlayerId, blackPlayerId));
    }

    @GetMapping("/{gameId}")
    public ResponseEntity<GameDTO> getGame(@PathVariable Long gameId) {
        return ResponseEntity.ok(gameService.getGame(gameId));
    }

    @PostMapping("/{gameId}/start")
    public ResponseEntity<GameDTO> startGame(@PathVariable Long gameId) {
        return ResponseEntity.ok(gameService.startGame(gameId));
    }

    @PutMapping("/{gameId}/state")
    public ResponseEntity<GameDTO> updateGameState(
            @PathVariable Long gameId,
            @RequestParam String fen,
            @RequestParam String pgn) {
        return ResponseEntity.ok(gameService.updateGameState(gameId, fen, pgn));
    }

    @PostMapping("/{gameId}/end")
    public ResponseEntity<GameDTO> endGame(
            @PathVariable Long gameId,
            @RequestParam String result,
            @RequestParam(required = false) Long winnerId) {
        Game.GameResult gameResult = Game.GameResult.valueOf(result);
        return ResponseEntity.ok(gameService.endGame(gameId, gameResult, winnerId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<GameDTO>> getUserGames(
            @PathVariable Long userId,
            Pageable pageable) {
        return ResponseEntity.ok(gameService.getUserGameHistory(userId, pageable));
    }

    @GetMapping("/pending")
    public ResponseEntity<Page<GameDTO>> getPendingGames(Pageable pageable) {
        return ResponseEntity.ok(gameService.getPendingGames(pageable));
    }
}
