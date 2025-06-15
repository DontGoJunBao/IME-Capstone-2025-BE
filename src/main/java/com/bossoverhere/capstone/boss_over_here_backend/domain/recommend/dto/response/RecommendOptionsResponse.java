package com.bossoverhere.capstone.boss_over_here_backend.domain.recommend.dto.response;

import com.bossoverhere.capstone.boss_over_here_backend.domain.spot.dto.response.SpotSimpleResponse;
import com.bossoverhere.capstone.boss_over_here_backend.domain.valuepick.dto.response.RecommendValuePickResponse;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(name="RecommendOptionsResponse", description="문항 + 스팟 목록을 함께 담아서 반환")
public record RecommendOptionsResponse(
        @Schema(description="클러스터 문항 + 보기 목록")
        List<RecommendValuePickResponse> valuePicks,

        @Schema(description="추천 시작 위치(스팟) 목록")
        List<SpotSimpleResponse> spots
) {}