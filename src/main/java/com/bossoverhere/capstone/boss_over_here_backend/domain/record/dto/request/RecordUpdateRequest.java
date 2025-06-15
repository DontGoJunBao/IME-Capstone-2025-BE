package com.bossoverhere.capstone.boss_over_here_backend.domain.record.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.time.LocalTime;
import java.util.Optional;

@Schema(description = "기록 부분 수정 요청 DTO (PATCH)")
public record RecordUpdateRequest(

        @Schema(
                description = "수정할 스팟 ID (생략 시 변경 없음)",
                example     = "42"
        )
        Optional<@Min(value = 1, message = "스팟 ID는 1 이상의 값이어야 합니다.") Long> spotId,

        @Schema(
                description = "수정할 시작 시간 (HH:mm, 00:00~23:59, 생략 시 변경 없음)",
                example     = "10:00",
                pattern     = "^([01]\\d|2[0-3]):[0-5]\\d$"
        )
        Optional<LocalTime> startTime,

        @Schema(
                description = "수정할 종료 시간 (HH:mm, 00:00~23:59, 생략 시 변경 없음)",
                example     = "19:00",
                pattern     = "^([01]\\d|2[0-3]):[0-5]\\d$"
        )
        Optional<LocalTime> endTime,

        @Schema(
                description = "수정할 매출 (0원 이상, 생략 시 변경 없음)",
                example     = "200000",
                minimum     = "0"
        )
        @Min(value = 0, message = "매출은 0원 이상이어야 합니다.")
        Optional<Long> revenue,

        @Schema(
                description = "수정할 지출 (0원 이상, 생략 시 변경 없음)",
                example     = "50000",
                minimum     = "0"
        )
        @Min(value = 0, message = "지출은 0원 이상이어야 합니다.")
        Optional<Long> expense,

        @Schema(
                description = "수정할 메모 (최대 300자, 생략 시 변경 없음)",
                example     = "저녁에도 손님이 많았습니다."
        )
        @Size(max = 300, message = "메모는 최대 300자까지 가능합니다.")
        Optional<String> memo

) { }