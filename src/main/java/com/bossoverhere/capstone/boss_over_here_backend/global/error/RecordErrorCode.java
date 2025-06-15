package com.bossoverhere.capstone.boss_over_here_backend.global.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum RecordErrorCode implements ErrorCode {
    INACTIVE_RECORD   (HttpStatus.FORBIDDEN,    "해당 기록은 비활성 상태입니다."),
    NOTFOUND_RECORD   (HttpStatus.NOT_FOUND,    "해당 ID의 기록을 찾을 수 없습니다."),
    ACCESS_DENIED     (HttpStatus.FORBIDDEN,    "본인의 기록만 조회할 수 있습니다."),
    INVALID_TIME      (HttpStatus.BAD_REQUEST,  "시작 시간은 종료 시간 이전이어야 합니다."),
    INVALID_AMOUNT    (HttpStatus.BAD_REQUEST,  "금액은 0원 이상이어야 합니다."),
    INVALID_DATE      (HttpStatus.BAD_REQUEST,  "기록 날짜는 오늘 이전 또는 오늘이어야 합니다."),
    ;
    private final HttpStatus httpStatus;
    private final String message;
}