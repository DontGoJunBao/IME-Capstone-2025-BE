package com.bossoverhere.capstone.boss_over_here_backend.domain.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class OauthLoginResponse {
    private String accessToken;
    private String refreshToken;
}