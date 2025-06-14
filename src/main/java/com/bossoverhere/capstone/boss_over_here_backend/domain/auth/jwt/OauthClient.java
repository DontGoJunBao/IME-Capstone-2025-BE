package com.bossoverhere.capstone.boss_over_here_backend.domain.auth.jwt;

public interface OauthClient {
    String getOAuthProviderUserId(String accessToken);
}