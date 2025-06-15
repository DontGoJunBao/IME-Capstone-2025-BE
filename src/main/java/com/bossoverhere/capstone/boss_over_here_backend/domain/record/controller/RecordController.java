package com.bossoverhere.capstone.boss_over_here_backend.domain.record.controller;

import com.bossoverhere.capstone.boss_over_here_backend.domain.record.domain.Record;
import com.bossoverhere.capstone.boss_over_here_backend.domain.record.dto.request.RecordCreateRequest;
import com.bossoverhere.capstone.boss_over_here_backend.domain.record.dto.request.RecordUpdateRequest;
import com.bossoverhere.capstone.boss_over_here_backend.domain.record.dto.response.CalendarDateResponse;
import com.bossoverhere.capstone.boss_over_here_backend.domain.record.dto.response.RecordResponse;
import com.bossoverhere.capstone.boss_over_here_backend.domain.record.dto.response.RecordSummaryResponse;
import com.bossoverhere.capstone.boss_over_here_backend.domain.record.service.RecordService;
import com.bossoverhere.capstone.boss_over_here_backend.global.common.dto.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Tag(name = "record", description = "기록 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/records")
public class RecordController {
    private final RecordService recordService;

    @GetMapping("/calendar-dates")
    @Operation(summary = "월별 기록 캘린더 조회",
            description = "년(year)과 월(month)을 지정해, 해당 월에 기록이 있는 날짜 목록을 반환합니다.")
    public ResponseEntity<CommonResponse<List<CalendarDateResponse>>> getCalendarDates(
            @RequestParam int year,
            @RequestParam int month,
            @AuthenticationPrincipal Long userId
    ) {
        List<LocalDate> dates = recordService.getCalendarDates(userId, year, month);
        List<CalendarDateResponse> dto = dates.stream()
                .map(d -> new CalendarDateResponse(d, true))
                .collect(Collectors.toList());
        return ResponseEntity.ok(CommonResponse.createSuccess(dto));
    }

    @GetMapping
    @Operation(summary = "일별 기록 리스트 조회",
            description = "date 파라미터로 지정된 날짜의 모든 기록 요약을 반환합니다.")
    public ResponseEntity<CommonResponse<List<RecordSummaryResponse>>> getRecordsByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @AuthenticationPrincipal Long userId
    ) {
        List<Record> records = recordService.getRecordsByDate(userId, date);
        List<RecordSummaryResponse> dto = records.stream()
                .map(RecordSummaryResponse::of)
                .collect(Collectors.toList());
        return ResponseEntity.ok(CommonResponse.createSuccess(dto));
    }

    @GetMapping("/spots/{spotId}")
    @Operation(summary = "스팟별 기록 조회",
            description = "로그인 유저가 해당 스팟에서 남긴 모든 기록 요약을 반환합니다.")
    public ResponseEntity<CommonResponse<List<RecordSummaryResponse>>> getRecordsBySpot(
            @PathVariable Long spotId,
            @AuthenticationPrincipal Long userId
    ) {
        List<Record> records = recordService.getRecordsBySpot(userId, spotId);
        List<RecordSummaryResponse> dto = records.stream()
                .map(RecordSummaryResponse::of)
                .collect(Collectors.toList());
        return ResponseEntity.ok(CommonResponse.createSuccess(dto));
    }



    @GetMapping("/{recordId}")
    @Operation(summary = "기록 상세 조회",
            description = "기록 ID(recordId)에 해당하는 상세 정보를 반환합니다.")
    public ResponseEntity<CommonResponse<RecordResponse>> getRecord(
            @PathVariable Long recordId,
            @AuthenticationPrincipal Long userId
    ) {
        Record record = recordService.getRecord(userId, recordId);
        RecordResponse dto = RecordResponse.of(record);
        return ResponseEntity.ok(CommonResponse.createSuccess(dto));
    }


    @PostMapping
    @Operation(
            summary = "기록 생성",
            description = "새로운 기록을 생성합니다."
    )
    public ResponseEntity<CommonResponse<Void>> createRecord(
            @RequestBody @Valid RecordCreateRequest request,
            @AuthenticationPrincipal Long userId
    ) {
        recordService.create(userId, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(CommonResponse.createSuccessWithNoContent("기록 생성이 완료되었습니다."));
    }

    @PatchMapping("/{recordId}")
    @Operation(
            summary = "기록 부분 수정",
            description = "기록 ID(recordId)에 해당하는 필드들을 선택적으로 수정합니다."
    )
    public ResponseEntity<CommonResponse<Void>> updateRecord(
            @PathVariable Long recordId,
            @RequestBody RecordUpdateRequest request,
            @AuthenticationPrincipal Long userId
    ) {
        recordService.update(userId, recordId, request);
        return ResponseEntity
                .ok(CommonResponse.createSuccessWithNoContent("기록 수정이 완료되었습니다."));
    }

    @DeleteMapping("/{recordId}")
    @Operation(
            summary = "기록 삭제",
            description = "기록 ID(recordId)에 해당하는 기록을 삭제합니다."
    )
    public ResponseEntity<CommonResponse<Void>> deleteRecord(
            @PathVariable Long recordId,
            @AuthenticationPrincipal Long userId
    ) {
        recordService.delete(userId, recordId);
        return ResponseEntity
                .ok(CommonResponse.createSuccessWithNoContent("기록 삭제가 완료되었습니다."));
    }


}
