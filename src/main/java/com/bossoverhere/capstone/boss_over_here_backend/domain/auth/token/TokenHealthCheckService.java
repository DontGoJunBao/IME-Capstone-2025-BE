package com.bossoverhere.capstone.boss_over_here_backend.domain.auth.token;

import com.bossoverhere.capstone.boss_over_here_backend.domain.auth.jwt.JwtUtil;
import com.bossoverhere.capstone.boss_over_here_backend.global.error.ApplicationException;
import com.bossoverhere.capstone.boss_over_here_backend.global.error.SecurityErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenHealthCheckService {
    private final JwtUtil jwtUtil;

    public void healthCheck(String token) {
        try {
            jwtUtil.isExpired(token);
        } catch (Exception e) {
            throw new ApplicationException(SecurityErrorCode.EXPIRED_TOKEN);
        }
    }
}
