package com.bossoverhere.capstone.boss_over_here_backend.domain.valuepick.service;

import com.bossoverhere.capstone.boss_over_here_backend.domain.valuepick.domain.ValuePick;
import com.bossoverhere.capstone.boss_over_here_backend.domain.valuepick.domain.ValueType;
import com.bossoverhere.capstone.boss_over_here_backend.domain.valuepick.dto.request.RecommendValuePickRequest;
import com.bossoverhere.capstone.boss_over_here_backend.global.error.ApplicationException;
import com.bossoverhere.capstone.boss_over_here_backend.global.error.RecommendErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecommendValuePickServiceTest {

    @InjectMocks
    RecommendValuePickService service;

    @Mock
    ValuePickService valuePickService;

    // 헬퍼: Map 형태의 답안 가진 ValuePick 목 생성
    // 변경 후
    private ValuePick makeVp(ValueType type, Integer... keys) {
        ValuePick vp = mock(ValuePick.class);
        when(vp.getValueType()).thenReturn(type);
        Map<Integer,Object> answers = new HashMap<>();
        for (Integer k : keys) answers.put(k, "A"+k);
        when(vp.getAnswers()).thenReturn(answers);
        return vp;
    }

    @Test
    @DisplayName("정상: 정확히 1개 CLUSTER, 유효 답안")
    void valid_singleCluster() {
        // 요청도 1개
        var reqs = List.of(new RecommendValuePickRequest(1L, List.of(1)));
        // DB 에도 1개 CLUSTER
        var vps = List.of(makeVp( ValueType.CLUSTER, 1));
        when(valuePickService.getValuePicksByIds(any())).thenReturn(vps);
        // 세부 조회도 stub
        when(valuePickService.getValuePickById(1L)).thenReturn(vps.get(0));

        var picks = service.createAndValidatePicks(reqs);
        assertThat(picks).hasSize(1);
        assertThat(picks.get(0).getValuePick()).isEqualTo(vps.get(0));
        assertThat(picks.get(0).getSelectedAnswers()).containsExactly(1);
    }

    @Test @DisplayName("실패: CLUSTER 0개 → CLUSTER_SELECTION_INVALID")
    void noCluster_throws() {
        var reqs = List.of(new RecommendValuePickRequest(1L, List.of(1)));
        when(valuePickService.getValuePicksByIds(any())).thenReturn(List.of());
        assertThatThrownBy(() -> service.createAndValidatePicks(reqs))
                .isInstanceOf(ApplicationException.class)
                .extracting("errorCode")
                .isEqualTo(RecommendErrorCode.CLUSTER_SELECTION_INVALID);
    }

    private ValuePick makeTypeOnlyVp(ValueType type) {
        ValuePick vp = mock(ValuePick.class);
        when(vp.getValueType()).thenReturn(type);
        return vp;
    }

    @Test @DisplayName("실패: CLUSTER 2개 → CLUSTER_SELECTION_INVALID")
    void multipleCluster_throws() {
        var reqs = List.of(
                new RecommendValuePickRequest(1L, List.of(1)),
                new RecommendValuePickRequest(2L, List.of(1))
        );
        // 타입만 stub
        var vps = List.of(
                makeTypeOnlyVp(ValueType.CLUSTER),
                makeTypeOnlyVp(ValueType.CLUSTER)
        );
        when(valuePickService.getValuePicksByIds(any())).thenReturn(vps);

        assertThatThrownBy(() -> service.createAndValidatePicks(reqs))
                .isInstanceOf(ApplicationException.class)
                .extracting("errorCode")
                .isEqualTo(RecommendErrorCode.CLUSTER_SELECTION_INVALID);
    }

    @Test @DisplayName("실패: 선택 답안에 유효하지 않은 번호 포함 → ANSWER_OPTION_INVALID")
    void invalidAnswer_throws() {
        var reqs = List.of(new RecommendValuePickRequest(1L, List.of(99)));
        var vp = makeVp(ValueType.CLUSTER, 1,2);
        when(valuePickService.getValuePicksByIds(any())).thenReturn(List.of(vp));
        when(valuePickService.getValuePickById(1L)).thenReturn(vp);

        assertThatThrownBy(() -> service.createAndValidatePicks(reqs))
                .isInstanceOf(ApplicationException.class)
                .extracting("errorCode")
                .isEqualTo(RecommendErrorCode.ANSWER_OPTION_INVALID);
    }
}