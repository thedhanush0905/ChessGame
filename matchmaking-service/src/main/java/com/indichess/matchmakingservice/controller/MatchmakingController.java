package com.indichess.matchmakingservice.controller;

import com.indichess.matchmakingservice.model.MatchQueue;
import com.indichess.matchmakingservice.service.MatchmakingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/matchmaking")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class MatchmakingController {

    private final MatchmakingService matchmakingService;

    @PostMapping("/join")
    public ResponseEntity<?> joinQueue(
            @RequestParam Long userId,
            @RequestParam String timeControl,
            @RequestParam Integer skillLevel) {
        try {
            MatchQueue result = matchmakingService.joinQueue(userId, timeControl, skillLevel);
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/leave")
    public ResponseEntity<?> leaveQueue(@RequestParam Long userId) {
        try {
            matchmakingService.leaveQueue(userId);
            return ResponseEntity.ok("Left queue");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/status/{userId}")
    public ResponseEntity<?> getStatus(@PathVariable Long userId) {
        MatchQueue status = matchmakingService.getQueueStatus(userId);
        if (status != null) {
            return ResponseEntity.ok(status);
        }
        return ResponseEntity.notFound().build();
    }
}
