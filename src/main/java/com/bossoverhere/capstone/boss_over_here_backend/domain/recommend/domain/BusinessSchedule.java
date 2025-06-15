package com.bossoverhere.capstone.boss_over_here_backend.domain.recommend.domain;

import com.bossoverhere.capstone.boss_over_here_backend.global.error.ApplicationException;
import com.bossoverhere.capstone.boss_over_here_backend.global.error.RecommendErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BusinessSchedule {
    @Column(nullable=false)
    private LocalDate date;
    @Column(nullable=false)
    private LocalTime startTime;
    @Column(nullable=false)
    private LocalTime endTime;


    @Builder
    public BusinessSchedule(LocalDate date, LocalTime startTime, LocalTime endTime) {

        if (endTime.isBefore(startTime)) {
            throw new ApplicationException(RecommendErrorCode.INVALID_TIME_RANGE);
        }
        this.date = date;
        this.startTime = startTime;
        this.endTime   = endTime;
    }

}