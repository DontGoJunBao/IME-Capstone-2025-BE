package com.bossoverhere.capstone.boss_over_here_backend.domain.ai.dto.response;

import java.util.List;

public record AiPlanResponse(
        List<PlanItem> plan
) {
}
