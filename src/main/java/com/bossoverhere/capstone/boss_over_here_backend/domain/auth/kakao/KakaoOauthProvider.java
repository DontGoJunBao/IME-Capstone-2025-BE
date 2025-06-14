package com.bossoverhere.capstone.boss_over_here_backend.domain.auth.kakao;


import com.bossoverhere.capstone.boss_over_here_backend.domain.auth.OauthClient;
import com.bossoverhere.capstone.boss_over_here_backend.domain.auth.OauthProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KakaoOauthProvider implements OauthProvider {
    private final KakaoOauthClient kakaoOauthClient;

    @Override
    public OauthType getOauthType() {
        return OauthType.KAKAO;
    }

    @Override
    public OauthClient getOAuthClient() {
        return kakaoOauthClient;
    }
}