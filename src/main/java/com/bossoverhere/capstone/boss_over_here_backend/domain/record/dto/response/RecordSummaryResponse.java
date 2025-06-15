package com.bossoverhere.capstone.boss_over_here_backend.domain.record.dto.response;

import com.bossoverhere.capstone.boss_over_here_backend.domain.record.domain.Record;

import java.time.LocalDate;

public record RecordSummaryResponse(
        Long      id,
        LocalDate recordDate,
        Long      spotId,
        String    spotName,

        Long      revenue

) {
    public static RecordSummaryResponse of(Record r) {
        return new RecordSummaryResponse(
                r.getId(),
                r.getRecordDate(),
                r.getSpot().getId(),
                r.getSpot().getName(),
                r.getRevenue()

        );
    }
}