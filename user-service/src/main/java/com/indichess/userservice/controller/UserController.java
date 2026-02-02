package com.indichess.userservice.controller;

import com.indichess.userservice.dto.UserProfileDTO;
import com.indichess.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {

    private final UserService userService;

    @PostMapping("/{userId}")
    public ResponseEntity<UserProfileDTO> createProfile(
            @PathVariable Long userId,
            @RequestParam String username,
            @RequestParam String email) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.createUserProfile(userId, username, email));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserProfileDTO> getProfile(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUserProfile(userId));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserProfileDTO> updateProfile(
            @PathVariable Long userId,
            @RequestBody UserProfileDTO dto) {
        return ResponseEntity.ok(userService.updateUserProfile(userId, dto));
    }

    @GetMapping("/leaderboard")
    public ResponseEntity<Page<UserProfileDTO>> getLeaderboard(Pageable pageable) {
        return ResponseEntity.ok(userService.getLeaderboard(pageable));
    }

    @PostMapping("/{userId}/stats")
    public ResponseEntity<UserProfileDTO> updateStats(
            @PathVariable Long userId,
            @RequestParam int result) {
        return ResponseEntity.ok(userService.updateStats(userId, result));
    }
}
