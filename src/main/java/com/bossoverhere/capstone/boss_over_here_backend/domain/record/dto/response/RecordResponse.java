package com.bossoverhere.capstone.boss_over_here_backend.domain.record.dto.response;

import com.bossoverhere.capstone.boss_over_here_backend.domain.record.domain.Record;

import java.time.LocalDate;
import java.time.LocalTime;

public record RecordResponse(
        Long id,
        LocalDate recordDate,
        LocalTime startTime,
        LocalTime endTime,
        Long      spotId,
        String    spotName,
        Long      revenue,
        Long      expense,
        Long      profit,
        String    memo
) {
    public static RecordResponse of(Record r) {
        return new RecordResponse(
                r.getId(),
                r.getRecordDate(),
                r.getStartTime(),
                r.getEndTime(),
                r.getSpot().getId(),
                r.getSpot().getName(),
                r.getRevenue(),
                r.getExpense(),
                r.calculateProfit(),
                r.getMemo()
        );
    }
}