package com.bossoverhere.capstone.boss_over_here_backend.domain.ai.dto.response;

public record PlanItem(
        String time,
        Long from,
        Long to
) {}