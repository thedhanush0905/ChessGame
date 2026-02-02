package com.indichess.userservice.service;

import com.indichess.userservice.dto.UserProfileDTO;
import com.indichess.userservice.model.UserProfile;
import com.indichess.userservice.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserProfileRepository userProfileRepository;

    public UserProfileDTO createUserProfile(Long userId, String username, String email) {
        UserProfile profile = new UserProfile();
        profile.setUserId(userId);
        profile.setUsername(username);
        profile.setEmail(email);
        profile.setEloRating(1200);
        profile.setTotalGames(0);
        profile.setWins(0);
        profile.setLosses(0);
        profile.setDraws(0);

        UserProfile saved = userProfileRepository.save(profile);
        log.info("User profile created for user: {}", userId);
        return mapToDTO(saved);
    }

    public UserProfileDTO getUserProfile(Long userId) {
        return userProfileRepository.findById(userId)
                .map(this::mapToDTO)
                .orElseThrow(() -> new RuntimeException("User profile not found"));
    }

    public UserProfileDTO updateUserProfile(Long userId, UserProfileDTO dto) {
        UserProfile profile = userProfileRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User profile not found"));

        if (dto.getFullName() != null) profile.setFullName(dto.getFullName());
        if (dto.getCountry() != null) profile.setCountry(dto.getCountry());
        if (dto.getBio() != null) profile.setBio(dto.getBio());
        if (dto.getProfilePictureUrl() != null) profile.setProfilePictureUrl(dto.getProfilePictureUrl());

        UserProfile updated = userProfileRepository.save(profile);
        return mapToDTO(updated);
    }

    public Page<UserProfileDTO> getLeaderboard(Pageable pageable) {
        return userProfileRepository.findLeaderboard(pageable)
                .map(this::mapToDTO);
    }

    public UserProfileDTO updateStats(Long userId, int result) {
        // result: 1 = win, 0 = draw, -1 = loss
        UserProfile profile = userProfileRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User profile not found"));

        profile.setTotalGames(profile.getTotalGames() + 1);
        if (result == 1) {
            profile.setWins(profile.getWins() + 1);
            profile.setEloRating(profile.getEloRating() + 15);
        } else if (result == 0) {
            profile.setDraws(profile.getDraws() + 1);
            profile.setEloRating(profile.getEloRating() + 5);
        } else {
            profile.setLosses(profile.getLosses() + 1);
            profile.setEloRating(Math.max(0, profile.getEloRating() - 10));
        }

        UserProfile updated = userProfileRepository.save(profile);
        return mapToDTO(updated);
    }

    private UserProfileDTO mapToDTO(UserProfile profile) {
        return new UserProfileDTO(
                profile.getUserId(),
                profile.getUsername(),
                profile.getEmail(),
                profile.getFullName(),
                profile.getCountry(),
                profile.getBio(),
                profile.getProfilePictureUrl(),
                profile.getEloRating(),
                profile.getTotalGames(),
                profile.getWins(),
                profile.getLosses(),
                profile.getDraws()
        );
    }
}
