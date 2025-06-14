package com.bossoverhere.capstone.boss_over_here_backend.domain.auth.dao;

import com.bossoverhere.capstone.boss_over_here_backend.domain.auth.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByUserId(Long userId);
}
