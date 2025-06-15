package com.bossoverhere.capstone.boss_over_here_backend.domain.recommend.dto.request;

import com.bossoverhere.capstone.boss_over_here_backend.domain.valuepick.dto.request.RecommendValuePickRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Schema(description = "추천 요청 DTO", title = "RecommendRequest")
public record RecommendRequest(

        @Schema(description = "클러스터 문항 응답 리스트")
        @NotEmpty(message = "문항 응답은 반드시 하나 이상이어야 합니다.")
        List<@Valid RecommendValuePickRequest> valuePicks,

        @Schema(description = "장사 날짜 (YYYY-MM-DD)", example = "2025-05-17")
        @NotNull(message = "날짜는 필수입니다.")
        LocalDate date,

        @Schema(description = "장사 시작 시간 (HH:mm)", example = "10:00")
        @NotNull(message = "시작 시간은 필수입니다.")
        LocalTime startTime,

        @Schema(description = "장사 종료 시간 (HH:mm)", example = "19:00")
        @NotNull(message = "종료 시간은 필수입니다.")
        LocalTime endTime,

        @Schema(description = "추천 시작 스팟 ID(시작 장소)", example = "42")
        @NotNull(message = "스팟 ID는 필수입니다.")
        Long spotId

) {}