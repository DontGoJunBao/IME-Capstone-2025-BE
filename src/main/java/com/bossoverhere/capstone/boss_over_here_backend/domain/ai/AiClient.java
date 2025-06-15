package com.bossoverhere.capstone.boss_over_here_backend.domain.ai;

import com.bossoverhere.capstone.boss_over_here_backend.domain.ai.dto.request.AiPlanRequest;
import com.bossoverhere.capstone.boss_over_here_backend.domain.ai.dto.response.AiPlanResponse;
import com.bossoverhere.capstone.boss_over_here_backend.domain.ai.dto.response.PlanItem;
import com.bossoverhere.capstone.boss_over_here_backend.global.error.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static com.bossoverhere.capstone.boss_over_here_backend.global.error.AiErrorCode.AI_CALL_FAILURE;

@Service
@RequiredArgsConstructor
public class AiClient {
    private final RestTemplate rt;

    // application.yml 에서 주입
    @Value("${ai.server.url}")
    private String aiServerUrl;

    public List<PlanItem> callAiForPlan(AiPlanRequest req) {
        try {
            AiPlanResponse resp = rt.postForObject(
                    aiServerUrl,
                    req,
                    AiPlanResponse.class
            );
            return resp.plan();
        } catch (RestClientException e) {
            throw new ApplicationException(AI_CALL_FAILURE);
        }
    }


}