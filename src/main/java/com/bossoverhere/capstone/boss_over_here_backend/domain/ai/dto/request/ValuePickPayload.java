package com.bossoverhere.capstone.boss_over_here_backend.domain.ai.dto.request;

import java.util.List;

public record ValuePickPayload(
        Long valuePickId,
        List<Integer> selectedAnswers
) {}
