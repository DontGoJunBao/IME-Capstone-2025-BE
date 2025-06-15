package com.bossoverhere.capstone.boss_over_here_backend.global.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum RecommendErrorCode implements ErrorCode {

    // --- Not Found ---
    RECOMMEND_NOT_FOUND(HttpStatus.NOT_FOUND, "추천 내역을 찾을 수 없습니다."),
    VALUE_PICK_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 문항을 찾을 수 없습니다."),

    // --- Validation Errors ---
    QUESTION_COUNT_MISMATCH(HttpStatus.BAD_REQUEST,
            "모든 문항에 답변해야 합니다."),  // reqs.size != questions.size()

    CLUSTER_SELECTION_INVALID(HttpStatus.BAD_REQUEST,
            "Cluster 타입은 반드시 하나만 선택해야 합니다."),

    INVALID_TIME_RANGE(HttpStatus.BAD_REQUEST,"종료 시간은 시작 시간 이후여야 합니다."),
    INVALID_DATE(HttpStatus.BAD_REQUEST,"과거 날짜는 선택할 수 없습니다."),

    ANSWER_OPTION_INVALID(HttpStatus.BAD_REQUEST,
            "유효하지 않은 선택지 번호입니다."),;



    private final HttpStatus httpStatus;
    private final String message;
}
