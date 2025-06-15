package com.bossoverhere.capstone.boss_over_here_backend.domain.recommend.dto.response;

import com.bossoverhere.capstone.boss_over_here_backend.domain.result.domain.Result;
import com.bossoverhere.capstone.boss_over_here_backend.domain.spot.dto.response.SpotRecommendResponse;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(title="추천 플랜 아이템", description="AI가 제안한 시간별 출발·도착 스팟 정보")
public record RecommendPlanItemResponse(
        @Schema(description="방문 시간 (HH:mm)", example="10:00")
        String time,

        @Schema(description="출발 스팟 정보")
        SpotRecommendResponse from,

        @Schema(description="도착 스팟 정보")
        SpotRecommendResponse to
) {
    public static RecommendPlanItemResponse fromResult(Result r) {
        SpotRecommendResponse fromDto = SpotRecommendResponse.of(r.getFromSpot());
        SpotRecommendResponse toDto   = SpotRecommendResponse.of(r.getToSpot());
        return new RecommendPlanItemResponse(r.getTime().toString(), fromDto, toDto);
    }
}