package com.bossoverhere.capstone.boss_over_here_backend.domain.auth.service;

import com.bossoverhere.capstone.boss_over_here_backend.domain.auth.token.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogoutService {

    private final RefreshTokenService refreshTokenService;

    public void logout(Long userId) {
        refreshTokenService.deleteRefreshToken(userId);
    }
}