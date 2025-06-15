package com.bossoverhere.capstone.boss_over_here_backend.domain.spot.dto.response;

import com.bossoverhere.capstone.boss_over_here_backend.domain.spot.domain.Spot;
import io.swagger.v3.oas.annotations.media.Schema;

public record SpotRecommendResponse(
        @Schema(description = "스팟 ID", example = "42")
        Long spotId,

        @Schema(description = "스팟 이름", example = "홍대입구역 4번출구")
        String spotName,

        @Schema(description = "스팟 주소", example = "서울 마포구 어쩌구로 123")
        String spotAddress
) {
    public static SpotRecommendResponse of(Spot s) {
        return new SpotRecommendResponse(s.getId(), s.getName(), s.getAddress());
    }
}
