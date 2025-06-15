package com.bossoverhere.capstone.boss_over_here_backend.domain.valuepick.dto.response;

import com.bossoverhere.capstone.boss_over_here_backend.domain.valuepick.domain.RecommendValuePick;
import com.bossoverhere.capstone.boss_over_here_backend.domain.valuepick.domain.ValuePick;
import com.bossoverhere.capstone.boss_over_here_backend.domain.valuepick.domain.ValueType;

import java.util.List;

public record RecommendValuePickResponse(
        Long valuePickId,
        ValueType type,
        String question,
        List<ValuePickAnswerResponse> answers
) {
    /** 옵션 조회용: ValuePick → DTO */
    public static RecommendValuePickResponse fromValuePick(ValuePick vp) {
        List<ValuePickAnswerResponse> answerList = vp.getAnswers().entrySet().stream()
                .map(e -> new ValuePickAnswerResponse(e.getKey(), e.getValue().toString()))
                .toList();
        return new RecommendValuePickResponse(
                vp.getId(),
                vp.getValueType(),
                vp.getQuestion(),
                answerList
        );
    }

    /** 추천 히스토리용: 실제 선택된 RecommendValuePick → DTO */
    public static RecommendValuePickResponse fromRecommendValuePick(RecommendValuePick rvp) {
        List<ValuePickAnswerResponse> ans = rvp.getSelectedAnswers().stream()
                .map(i -> new ValuePickAnswerResponse(i, rvp.getValuePick().getAnswers().get(i).toString()))
                .toList();
        return new RecommendValuePickResponse(
                rvp.getValuePick().getId(),
                rvp.getValuePick().getValueType(),
                rvp.getValuePick().getQuestion(),
                ans
        );
    }
}