package com.bossoverhere.capstone.boss_over_here_backend.domain.recommend.dto.response;


import com.bossoverhere.capstone.boss_over_here_backend.domain.valuepick.dto.response.RecommendValuePickResponse;
import io.swagger.v3.oas.annotations.media.Schema;


import java.time.LocalDate;

import java.util.List;

@Schema(description = "추천 히스토리 응답 DTO")
public record RecommendHistoryResponse(
        @Schema(description = "추천 ID", example = "123")
        Long recommendId,

        @Schema(description = "추천 날짜 (YYYY-MM-DD)", example = "2025-05-17")
        LocalDate date,

        @Schema(description = "선택한 클러스터 문항 응답")
        List<RecommendValuePickResponse> selectedClusters,

        @Schema(description = "추천 플랜 (시간대별 출발·도착 스팟 정보)")
        List<RecommendPlanItemResponse> planItems
) {}