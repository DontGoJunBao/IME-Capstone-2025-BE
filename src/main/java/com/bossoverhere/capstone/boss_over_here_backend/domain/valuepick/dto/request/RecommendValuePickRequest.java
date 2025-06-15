package com.bossoverhere.capstone.boss_over_here_backend.domain.valuepick.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

@Schema(description = "추천 문항별 사용자의 선택 응답 DTO", title = "RecommendValuePickRequest")
public record RecommendValuePickRequest(
        @Schema(
                description = "문항 식별자 (ValuePick ID)",
                example     = "1"
        )
        @NotNull(message = "valuePickId는 필수입니다.")
        Long valuePickId,

        @Schema(
                description = "사용자가 선택한 보기 번호 목록",
                example     = "[1]"
        )
        @NotNull(message = "selectedAnswers는 비어 있을 수 없습니다.")
        @Size(min = 1, message = "최소 하나 이상의 답변을 선택해야 합니다.")
        List<Integer> selectedAnswers
) {}