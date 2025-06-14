package com.bossoverhere.capstone.boss_over_here_backend.domain.auth.service;

import com.bossoverhere.capstone.boss_over_here_backend.domain.auth.OauthProvider;
import com.bossoverhere.capstone.boss_over_here_backend.domain.auth.OauthProviderResolver;
import com.bossoverhere.capstone.boss_over_here_backend.domain.auth.dto.request.OauthLoginRequest;
import com.bossoverhere.capstone.boss_over_here_backend.domain.auth.dto.response.OauthLoginResponse;
import com.bossoverhere.capstone.boss_over_here_backend.domain.user.dao.UserRepository;
import com.bossoverhere.capstone.boss_over_here_backend.domain.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OauthServiceTest {

    @InjectMocks
    private OauthService oauthService;

    @Mock
    private OauthProviderResolver oauthProviderResolver;

    @Mock
    private UserRepository userRepository;


    @Mock
    private AuthTokenGenerator authTokenGenerator;

    @Mock
    private OauthProvider oauthProvider;

    @Test
    @DisplayName("처음 로그인 시 회원가입 후 액세스 및 리프레시 토큰 반환")
    void testFirstLogin_Success() {
        // Given
        String providerName = "kakao";
        String oauthCredential = "testCredential";
        String oauthId = providerName + "12345";
        OauthLoginRequest request = new OauthLoginRequest(providerName, oauthCredential);

        when(oauthProviderResolver.find(providerName)).thenReturn(oauthProvider);
        when(oauthProvider.getOAuthProviderUserId(oauthCredential)).thenReturn("12345");
        when(userRepository.findByOauthId(oauthId)).thenReturn(Optional.empty());

        User newUser = User.builder().oauthId(oauthId).build();
        ReflectionTestUtils.setField(newUser, "id", 1L);
        when(userRepository.save(any(User.class))).thenReturn(newUser);

        when(authTokenGenerator.generate(1L, oauthId)).thenReturn(new AuthToken("accessToken", "refreshToken"));

        // When
        OauthLoginResponse response = oauthService.login(request);

        // Then
        assertThat(response.getAccessToken()).isEqualTo("accessToken");
        assertThat(response.getRefreshToken()).isEqualTo("refreshToken");
    }

    @Test
    @DisplayName("이미 가입된 유저가 로그인 시 액세스 및 리프레시 토큰 반환")
    void testNotFirstLogin_Success() {
        // Given
        String providerName = "kakao";
        String oauthCredential = "testCredential";
        String oauthId = providerName + "12345";
        OauthLoginRequest request = new OauthLoginRequest(providerName, oauthCredential);

        when(oauthProviderResolver.find(providerName)).thenReturn(oauthProvider);
        when(oauthProvider.getOAuthProviderUserId(oauthCredential)).thenReturn("12345");

        User existingUser = User.builder().oauthId(oauthId).build();
        ReflectionTestUtils.setField(existingUser, "id", 1L);
        when(userRepository.findByOauthId(oauthId)).thenReturn(Optional.of(existingUser));

        when(authTokenGenerator.generate(1L, oauthId)).thenReturn(new AuthToken("accessToken", "refreshToken"));

        // When
        OauthLoginResponse response = oauthService.login(request);

        // Then
        assertThat(response.getAccessToken()).isEqualTo("accessToken");
        assertThat(response.getRefreshToken()).isEqualTo("refreshToken");
    }

    @Test
    @DisplayName("존재하지 않는 Provider로 로그인 요청 시 실패")
    void testLogin_Failure() {
        // Given
        String providerName = "invalidProvider";
        String oauthCredential = "testCredential";
        OauthLoginRequest request = new OauthLoginRequest(providerName, oauthCredential);

        when(oauthProviderResolver.find(providerName)).thenThrow(new RuntimeException("Provider not found"));

        // When & Then
        assertThrows(RuntimeException.class, () -> oauthService.login(request));
    }
}