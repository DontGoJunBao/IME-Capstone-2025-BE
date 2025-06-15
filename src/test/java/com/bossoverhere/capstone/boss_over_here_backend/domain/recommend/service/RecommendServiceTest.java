package com.bossoverhere.capstone.boss_over_here_backend.domain.recommend.service;

import com.bossoverhere.capstone.boss_over_here_backend.domain.ai.AiClient;
import com.bossoverhere.capstone.boss_over_here_backend.domain.ai.dto.request.AiPlanRequest;
import com.bossoverhere.capstone.boss_over_here_backend.domain.ai.dto.response.PlanItem;
import com.bossoverhere.capstone.boss_over_here_backend.domain.recommend.dao.RecommendRepository;
import com.bossoverhere.capstone.boss_over_here_backend.domain.recommend.dto.request.RecommendRequest;
import com.bossoverhere.capstone.boss_over_here_backend.domain.recommend.dto.response.RecommendOptionsResponse;
import com.bossoverhere.capstone.boss_over_here_backend.domain.recommend.dto.response.RecommendPlanItemResponse;
import com.bossoverhere.capstone.boss_over_here_backend.domain.spot.domain.Spot;
import com.bossoverhere.capstone.boss_over_here_backend.domain.spot.service.SpotService;
import com.bossoverhere.capstone.boss_over_here_backend.domain.user.domain.User;
import com.bossoverhere.capstone.boss_over_here_backend.domain.user.service.UserService;
import com.bossoverhere.capstone.boss_over_here_backend.domain.valuepick.dto.response.RecommendValuePickResponse;
import com.bossoverhere.capstone.boss_over_here_backend.domain.valuepick.dto.response.ValuePickAnswerResponse;
import com.bossoverhere.capstone.boss_over_here_backend.domain.valuepick.service.RecommendValuePickService;
import com.bossoverhere.capstone.boss_over_here_backend.global.error.ApplicationException;
import com.bossoverhere.capstone.boss_over_here_backend.global.error.RecommendErrorCode;
import com.bossoverhere.capstone.boss_over_here_backend.global.error.SpotErrorCode;
import com.bossoverhere.capstone.boss_over_here_backend.global.error.UserErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecommendServiceTest {

    @InjectMocks
    private RecommendService service;

    @Mock
    private UserService userService;
    @Mock private RecommendValuePickService pickService;
    @Mock private RecommendRepository recommendRepo;
    @Mock private AiClient aiClient;
    @Mock private SpotService spotService;

    private User user;
    private RecommendRequest dto;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).oauthId("oauth").build();
        dto = new RecommendRequest(
                List.of(),
                LocalDate.of(2025,5,17),
                LocalTime.of(10,0),
                LocalTime.of(19,0),
                42L
        );
    }

    @Test
    @DisplayName("1) 추천 성공 플로우")
    void recommend_success() {
        when(userService.getUserById(1L)).thenReturn(user);
        when(pickService.createAndValidatePicks(dto.valuePicks())).thenReturn(List.of());

        Spot start = Spot.builder()
                .id(42L).name("S").address("A").latitude(0).longitude(0).build();
        Spot t = Spot.builder()
                .id(43L).name("T").address("B").latitude(0).longitude(0).build();
        when(spotService.getSpotById(42L)).thenReturn(start);
        when(spotService.getSpotById(43L)).thenReturn(t);

        AiPlanRequest expectedReq = AiPlanRequest.from(
                dto.valuePicks(), dto.spotId(), dto.date(), dto.startTime(), dto.endTime()
        );

        List<PlanItem> items = List.of(
                new PlanItem("11:00", 42L, 43L),
                new PlanItem("12:00", 43L, 42L)
        );
        // buildRequest 대신 callAiForPlan(expectedReq) 을 stub
        when(aiClient.callAiForPlan(expectedReq)).thenReturn(items);

        // act
        var resp = service.recommend(1L, dto);

        // assert
        assertThat(resp)
                .extracting(RecommendPlanItemResponse::time)
                .containsExactly("11:00", "12:00");

        verify(recommendRepo).save(any());
    }


    @Test @DisplayName("2) 사용자 미존재 시 예외")
    void recommend_userNotFound() {
        when(userService.getUserById(99L))
                .thenThrow(new ApplicationException(UserErrorCode.NOTFOUND_USER));

        assertThatThrownBy(() -> service.recommend(99L, dto))
                .isInstanceOf(ApplicationException.class)
                .extracting("errorCode")
                .isEqualTo(UserErrorCode.NOTFOUND_USER);
    }

    @Test @DisplayName("3) 문항 검증 실패 시 예외")
    void recommend_invalidPicks() {
        when(userService.getUserById(1L)).thenReturn(user);
        when(pickService.createAndValidatePicks(dto.valuePicks()))
                .thenThrow(new ApplicationException(RecommendErrorCode.CLUSTER_SELECTION_INVALID));

        assertThatThrownBy(() -> service.recommend(1L, dto))
                .isInstanceOf(ApplicationException.class)
                .extracting("errorCode")
                .isEqualTo(RecommendErrorCode.CLUSTER_SELECTION_INVALID);
    }

    @Test @DisplayName("4) 스팟 미존재 시 예외")
    void recommend_spotNotFound() {
        when(userService.getUserById(1L)).thenReturn(user);
        when(pickService.createAndValidatePicks(dto.valuePicks())).thenReturn(List.of());
        when(spotService.getSpotById(42L))
                .thenThrow(new ApplicationException(SpotErrorCode.NOTFOUND_SPOT));

        assertThatThrownBy(() -> service.recommend(1L, dto))
                .isInstanceOf(ApplicationException.class)
                .extracting("errorCode")
                .isEqualTo(SpotErrorCode.NOTFOUND_SPOT);
    }

    @Test @DisplayName("5) AI 호출 실패 시 예외")
    void recommend_aiFailure() {
        // arrange
        when(userService.getUserById(1L)).thenReturn(user);
        when(pickService.createAndValidatePicks(dto.valuePicks())).thenReturn(List.of());
        Spot start = Spot.builder()
                .id(42L).name("S").address("A").latitude(0).longitude(0)
                .build();
        when(spotService.getSpotById(42L)).thenReturn(start);

        // AiPlanRequest.from(...) 으로 기대할 요청 객체 생성
        AiPlanRequest expectedReq = AiPlanRequest.from(
                dto.valuePicks(), dto.spotId(), dto.date(), dto.startTime(), dto.endTime()
        );

        // callAiForPlan(...) 에서 에러 던지도록 stub
        when(aiClient.callAiForPlan(eq(expectedReq)))
                .thenThrow(new ApplicationException(RecommendErrorCode.INVALID_TIME_RANGE));

        // act & assert
        assertThatThrownBy(() -> service.recommend(1L, dto))
                .isInstanceOf(ApplicationException.class)
                .extracting("errorCode")
                .isEqualTo(RecommendErrorCode.INVALID_TIME_RANGE);
    }

    @Test @DisplayName("6) 옵션 조회 성공")
    void findAllValuePicksAndSpots_success() {
        when(userService.getUserById(2L)).thenReturn(user);
        var pickDto = new RecommendValuePickResponse(1L,null,"Q",List.of(new ValuePickAnswerResponse(1,"A")));
        when(pickService.getRecommendValuePickResponses()).thenReturn(List.of(pickDto));
        Spot sp = Spot.builder().id(1L).name("X").address("Y").latitude(0).longitude(0).build();
        when(spotService.findAll()).thenReturn(List.of(sp));

        RecommendOptionsResponse opts = service.findAllValuePicksAndSpots(2L);

        assertThat(opts.valuePicks()).containsExactly(pickDto);
        assertThat(opts.spots()).extracting("id","name")
                .containsExactly(tuple(1L,"X"));
    }

    @Test @DisplayName("7) 옵션 조회 시 사용자 미존재 예외")
    void findAllValuePicksAndSpots_userNotFound() {
        when(userService.getUserById(3L))
                .thenThrow(new ApplicationException(UserErrorCode.NOTFOUND_USER));

        assertThatThrownBy(() -> service.findAllValuePicksAndSpots(3L))
                .isInstanceOf(ApplicationException.class)
                .extracting("errorCode")
                .isEqualTo(UserErrorCode.NOTFOUND_USER);
    }
}