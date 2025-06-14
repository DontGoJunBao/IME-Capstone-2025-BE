package com.bossoverhere.capstone.boss_over_here_backend.domain.auth;

import capston.junbao.BossOverHerePrac.domain.auth.domain.enums.OauthType;

public interface OauthProvider {
    OauthType getOauthType();
    OauthClient getOAuthClient();

    default boolean match(String providerName){
        return providerName.equals(getOauthType().getTypeName());
    }

    default String getOAuthProviderUserId(String accessToken) {
        OauthClient client = getOAuthClient();
        return client.getOAuthProviderUserId(accessToken);
    }

}