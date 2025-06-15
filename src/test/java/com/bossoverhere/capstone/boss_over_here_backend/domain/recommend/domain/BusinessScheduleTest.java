package com.bossoverhere.capstone.boss_over_here_backend.domain.recommend.domain;

import com.bossoverhere.capstone.boss_over_here_backend.global.error.ApplicationException;
import com.bossoverhere.capstone.boss_over_here_backend.global.error.RecommendErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
class BusinessScheduleTest {

    @Test
    @DisplayName("빌더: 종료 시간이 시작 시간보다 이전일 때 ApplicationException 발생")
    void builder_endTimeBeforeStartTime_throwsApplicationException() {
        // when / then
        assertThatThrownBy(() ->
                BusinessSchedule.builder()
                        .date(LocalDate.of(2025, 5, 17))
                        .startTime(LocalTime.of(12, 0))
                        .endTime(LocalTime.of(10, 0))
                        .build()
        )
                .isInstanceOf(ApplicationException.class)
                .extracting("errorCode")
                .isEqualTo(RecommendErrorCode.INVALID_TIME_RANGE);
    }

    @Test
    @DisplayName("빌더: 정상적인 시간 범위일 때 객체 생성")
    void builder_validTimes_createsBusinessSchedule() {
        LocalDate date = LocalDate.of(2025, 5, 17);
        LocalTime start = LocalTime.of(9, 0);
        LocalTime end = LocalTime.of(18, 0);

        BusinessSchedule schedule = BusinessSchedule.builder()
                .date(date)
                .startTime(start)
                .endTime(end)
                .build();

        assertThat(schedule.getDate()).isEqualTo(date);
        assertThat(schedule.getStartTime()).isEqualTo(start);
        assertThat(schedule.getEndTime()).isEqualTo(end);
    }
}