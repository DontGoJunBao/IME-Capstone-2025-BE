package com.bossoverhere.capstone.boss_over_here_backend.global.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SpotErrorCode implements ErrorCode {
    NOTFOUND_SPOT(HttpStatus.NOT_FOUND, "해당 스팟를 찾을 수 없습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
