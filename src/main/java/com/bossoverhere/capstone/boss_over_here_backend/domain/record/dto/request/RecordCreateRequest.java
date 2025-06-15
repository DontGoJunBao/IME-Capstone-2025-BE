package com.bossoverhere.capstone.boss_over_here_backend.domain.record.dto.request;

import com.bossoverhere.capstone.boss_over_here_backend.domain.record.domain.Record;
import com.bossoverhere.capstone.boss_over_here_backend.domain.spot.domain.Spot;
import com.bossoverhere.capstone.boss_over_here_backend.domain.user.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;

@Schema(description = "기록 생성 요청 DTO")
public record RecordCreateRequest(

        @Schema(
                description = "기록 날짜 (YYYY-MM-DD, 과거 또는 오늘까지 가능)",
                example     = "2025-05-10",
                pattern     = "^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$"
        )
        @NotNull(message = "날짜는 필수입니다.")
        @PastOrPresent(message = "기록 날짜는 오늘 이전 또는 오늘이어야 합니다.")
        LocalDate recordDate,

        @Schema(description = "스팟 ID", example = "42")
        @NotNull(message = "스팟은 필수입니다.")
        Long spotId,
        @Schema(
                description = "시작 시간 (HH:mm, 00:00~23:59)",
                example     = "09:00",
                pattern     = "^([01]\\d|2[0-3]):[0-5]\\d$"
        )
        @NotNull(message = "시작 시간은 필수입니다.")
        LocalTime startTime,

        @Schema(
                description = "종료 시간 (HH:mm, 00:00~23:59)",
                example     = "18:00",
                pattern     = "^([01]\\d|2[0-3]):[0-5]\\d$"
        )
        @NotNull(message = "종료 시간은 필수입니다.")
        LocalTime endTime,

        @Schema(
                description = "총 매출 (단위: 원, 0원 이상)",
                example     = "150000",
                minimum     = "0"
        )
        @NotNull(message = "매출은 필수값입니다.")
        @Min(value = 0, message = "매출은 0원 이상이어야 합니다.")
        Long revenue,

        @Schema(
                description = "총 지출 (단위: 원, 0원 이상)",
                example     = "45000",
                minimum     = "0"
        )
        @NotNull(message = "지출은 필수값입니다.")
        @Min(value = 0, message = "지출은 0원 이상이어야 합니다.")
        Long expense,

        @Schema(
                description = "메모 (최대 300자, 생략 가능)",
                example     = "점심 시간에 손님이 많았습니다."
        )
        @Size(max = 300, message = "메모는 최대 300자까지 가능합니다.")
        String memo

) {
    public Record toEntity(User user, Spot spot) {
        return Record.builder()
                .user(user)
                .recordDate(recordDate)
                .spot(spot)
                .startTime(startTime)
                .endTime(endTime)
                .revenue(revenue)
                .expense(expense)
                .memo(memo)
                .build();
    }
}
