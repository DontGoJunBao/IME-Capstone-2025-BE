package com.bossoverhere.capstone.boss_over_here_backend.domain.recommend.service;

import com.bossoverhere.capstone.boss_over_here_backend.domain.ai.AiClient;
import com.bossoverhere.capstone.boss_over_here_backend.domain.ai.dto.request.AiPlanRequest;
import com.bossoverhere.capstone.boss_over_here_backend.domain.ai.dto.response.PlanItem;
import com.bossoverhere.capstone.boss_over_here_backend.domain.recommend.dao.RecommendRepository;
import com.bossoverhere.capstone.boss_over_here_backend.domain.recommend.domain.BusinessSchedule;
import com.bossoverhere.capstone.boss_over_here_backend.domain.recommend.domain.Recommend;
import com.bossoverhere.capstone.boss_over_here_backend.domain.recommend.dto.request.RecommendRequest;
import com.bossoverhere.capstone.boss_over_here_backend.domain.recommend.dto.response.RecommendHistoryResponse;
import com.bossoverhere.capstone.boss_over_here_backend.domain.recommend.dto.response.RecommendOptionsResponse;
import com.bossoverhere.capstone.boss_over_here_backend.domain.recommend.dto.response.RecommendPlanItemResponse;
import com.bossoverhere.capstone.boss_over_here_backend.domain.result.domain.Result;
import com.bossoverhere.capstone.boss_over_here_backend.domain.spot.domain.Spot;
import com.bossoverhere.capstone.boss_over_here_backend.domain.spot.dto.response.SpotSimpleResponse;
import com.bossoverhere.capstone.boss_over_here_backend.domain.spot.service.SpotService;
import com.bossoverhere.capstone.boss_over_here_backend.domain.user.domain.User;
import com.bossoverhere.capstone.boss_over_here_backend.domain.user.service.UserService;
import com.bossoverhere.capstone.boss_over_here_backend.domain.valuepick.domain.RecommendValuePick;
import com.bossoverhere.capstone.boss_over_here_backend.domain.valuepick.domain.ValueType;
import com.bossoverhere.capstone.boss_over_here_backend.domain.valuepick.dto.response.RecommendValuePickResponse;
import com.bossoverhere.capstone.boss_over_here_backend.domain.valuepick.service.RecommendValuePickService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecommendService {

    private final UserService userService;
    private final RecommendValuePickService recommendValuePickService;
    private final RecommendRepository recommendRepository;
    private final AiClient aiClient;
    private final SpotService spotService;

    /**
     * 추천 생성
     */
    @Transactional
    public List<RecommendPlanItemResponse> recommend(Long userId, RecommendRequest dto) {
        // 1) 사용자 검증
        User user = userService.getUserById(userId);

        // 2) 문항 검증 및 엔티티 변환
        List<RecommendValuePick> picks = recommendValuePickService.createAndValidatePicks(dto.valuePicks());

        // 3) 스팟 검증
        Spot startSpot = spotService.getSpotById(dto.spotId());

        // 4) 일정 엔베디드 생성
        BusinessSchedule schedule = BusinessSchedule.builder()
                .date(dto.date())
                .startTime(dto.startTime())
                .endTime(dto.endTime())
                .build();

        // 5) Recommend 생성 및 저장
        Recommend recommend = Recommend.createRecommend(user, picks, schedule, startSpot);
        recommendRepository.save(recommend);

        // 6) AI 요청용 DTO 생성
        AiPlanRequest req = AiPlanRequest.from(
                dto.valuePicks(), dto.spotId(), dto.date(), dto.startTime(), dto.endTime()
        );

        // 7) AI 호출
        List<PlanItem> planItems = aiClient.callAiForPlan(req);

        // 8) Result 엔티티 생성 및 저장
        planItems.stream()
                .map(this::planItemToResult)
                .forEach(recommend::addResult);


        // 9) 저장된 Result 엔티티를 기반으로 응답 생성
        return recommend.getResults().stream()
                .map(RecommendPlanItemResponse::fromResult)
                .collect(Collectors.toList());
    }



    /**
     * 추천 옵션 조회
     */
    public RecommendOptionsResponse findAllValuePicksAndSpots(Long userId) {
        userService.getUserById(userId);

        List<RecommendValuePickResponse> picks = recommendValuePickService.getRecommendValuePickResponses();
        List<SpotSimpleResponse> spots = spotService.findAll().stream()
                .map(SpotSimpleResponse::of)
                .toList();

        return new RecommendOptionsResponse(picks, spots);
    }

    /**
     * 추천 이력 조회
     */
    public List<RecommendHistoryResponse> getAllRecommendHistory(Long userId) {
        User user = userService.getUserById(userId);
        return user.getRecommends().stream()
                .map(this::toHistoryResponse)
                .toList();


    }

    private Result planItemToResult(PlanItem pi) {
        Spot fromSpot = spotService.getSpotById(pi.from());
        Spot toSpot   = spotService.getSpotById(pi.to());
        return Result.create(LocalTime.parse(pi.time()), fromSpot, toSpot);
    }

    /**
     * 추천 이력 응답 매핑
     */
    private RecommendHistoryResponse toHistoryResponse(Recommend recommend) {
        return new RecommendHistoryResponse(
                recommend.getId(),
                recommend.getBusinessSchedule().getDate(),
                toClusterResponses(recommend),
                toPlanResponses(recommend)
        );
    }

    /** 클러스터 응답 매핑 */
    private List<RecommendValuePickResponse> toClusterResponses(Recommend recommend) {
        return recommend.getRecommendValuePicks().stream()
                .filter(vp -> vp.getValuePick().getValueType() == ValueType.CLUSTER)
                .map(RecommendValuePickResponse::fromRecommendValuePick)
                .toList();
    }

    /** PlanItem 응답 매핑 */
    private List<RecommendPlanItemResponse> toPlanResponses(Recommend recommend) {
        return recommend.getResults().stream()
                .map(RecommendPlanItemResponse::fromResult)
                .toList();
    }
}
