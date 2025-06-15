package com.bossoverhere.capstone.boss_over_here_backend.domain.valuepick.service;

import com.bossoverhere.capstone.boss_over_here_backend.domain.valuepick.domain.RecommendValuePick;
import com.bossoverhere.capstone.boss_over_here_backend.domain.valuepick.domain.ValuePick;
import com.bossoverhere.capstone.boss_over_here_backend.domain.valuepick.domain.ValueType;
import com.bossoverhere.capstone.boss_over_here_backend.domain.valuepick.dto.request.RecommendValuePickRequest;
import com.bossoverhere.capstone.boss_over_here_backend.domain.valuepick.dto.response.RecommendValuePickResponse;
import com.bossoverhere.capstone.boss_over_here_backend.global.error.ApplicationException;
import com.bossoverhere.capstone.boss_over_here_backend.global.error.RecommendErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecommendValuePickService {

    private final ValuePickService valuePickService;

    /** 추천 문항 검증 */
    @Transactional
    public List<RecommendValuePick> createAndValidatePicks(List<RecommendValuePickRequest> reqs) {
        // 1) 요청 ID로 DB에서 ValuePick 엔티티 조회
        List<Long> ids = reqs.stream()
                .map(RecommendValuePickRequest::valuePickId)
                .toList();
        List<ValuePick> valuePicks = valuePickService.getValuePicksByIds(ids);

        // A) “문항을 한 번도 안 골랐거나 여러 개 골랐는지” 검증
        ensureExactlyOneCluster(valuePicks);

        // B) 선택 번호 유효성 검증 → 엔티티 변환
        return reqs.stream()
                .map(this::toRecommendValuePick)
                .toList();
    }

    /** 추천 문항 리스트 조회 */
    public List<RecommendValuePickResponse> getRecommendValuePickResponses() {
        return valuePickService.getAllValuePicks().stream()
                .map(RecommendValuePickResponse::fromValuePick)
                .toList();
    }

    /** 클러스터 문항이 정확히 1개인지 검증 */
    private void ensureExactlyOneCluster(List<ValuePick> valuePicks) {
        long clusterCount = valuePicks.stream()
                .filter(vp -> vp.getValueType() == ValueType.CLUSTER)
                .count();
        if (clusterCount != 1) {
            throw new ApplicationException(RecommendErrorCode.CLUSTER_SELECTION_INVALID);
        }
    }

    /** 하나의 요청(Request) → RecommendValuePick 엔티티로 변환 */
    private RecommendValuePick toRecommendValuePick(RecommendValuePickRequest req) {
        // ① ValuePick 조회
        ValuePick vp = valuePickService.getValuePickById(req.valuePickId());
        // ② 선택 답안 검증
        validateAnswers(vp, req.selectedAnswers());
        // ③ 엔티티 빌드
        return RecommendValuePick.builder()
                .valuePick(vp)
                .selectedAnswers(req.selectedAnswers())
                .build();
    }

    /** ValuePick 의 answers 키에 요청된 selectedAnswers 가 모두 포함되어 있는지 검증 */
    private void validateAnswers(ValuePick vp, List<Integer> selectedAnswers) {
        Set<Integer> validKeys = vp.getAnswers().keySet();
        if (!validKeys.containsAll(selectedAnswers)) {
            throw new ApplicationException(RecommendErrorCode.ANSWER_OPTION_INVALID);
        }
    }
}