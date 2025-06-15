package com.bossoverhere.capstone.boss_over_here_backend.domain.record.dto.response;

import java.time.LocalDate;

public record CalendarDateResponse(
        LocalDate date,
        boolean   hasRecord
) { }