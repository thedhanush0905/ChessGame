package com.indichess.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDTO {
    private Long userId;
    private String username;
    private String email;
    private String fullName;
    private String country;
    private String bio;
    private String profilePictureUrl;
    private Integer eloRating;
    private Integer totalGames;
    private Integer wins;
    private Integer losses;
    private Integer draws;
}
