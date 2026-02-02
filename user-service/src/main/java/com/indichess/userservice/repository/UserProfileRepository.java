package com.indichess.userservice.repository;

import com.indichess.userservice.model.UserProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    Optional<UserProfile> findByUsername(String username);
    Optional<UserProfile> findByEmail(String email);
    
    @Query("SELECT u FROM UserProfile u ORDER BY u.eloRating DESC")
    Page<UserProfile> findLeaderboard(Pageable pageable);
}
