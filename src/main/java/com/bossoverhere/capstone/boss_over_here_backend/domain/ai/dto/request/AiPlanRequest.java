package com.bossoverhere.capstone.boss_over_here_backend.domain.ai.dto.request;

import com.bossoverhere.capstone.boss_over_here_backend.domain.valuepick.dto.request.RecommendValuePickRequest;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public record AiPlanRequest(
        List<ValuePickPayload> valuePicks,
        Long startSpotId,
        String date,
        String startTime,
        String endTime
) {
    public static AiPlanRequest from(
            List<RecommendValuePickRequest> picks,
            Long startSpotId,
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime
    ) {
        return new AiPlanRequest(
                picks.stream()
                        .map(p -> new ValuePickPayload(p.valuePickId(), p.selectedAnswers()))
                        .toList(),
                startSpotId,
                date.format(DateTimeFormatter.ISO_DATE),
                startTime.format(DateTimeFormatter.ISO_TIME),
                endTime.format(DateTimeFormatter.ISO_TIME)
        );
    }
}