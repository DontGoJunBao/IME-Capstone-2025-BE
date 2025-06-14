package com.bossoverhere.capstone.boss_over_here_backend.domain.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RefreshedTokensResponse {
    private String accessToken;
    private String refreshToken;
}