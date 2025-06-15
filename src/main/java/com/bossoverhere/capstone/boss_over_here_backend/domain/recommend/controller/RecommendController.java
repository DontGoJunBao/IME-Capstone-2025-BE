package com.bossoverhere.capstone.boss_over_here_backend.domain.recommend.controller;

import com.bossoverhere.capstone.boss_over_here_backend.domain.recommend.dto.request.RecommendRequest;
import com.bossoverhere.capstone.boss_over_here_backend.domain.recommend.dto.response.RecommendHistoryResponse;
import com.bossoverhere.capstone.boss_over_here_backend.domain.recommend.dto.response.RecommendOptionsResponse;
import com.bossoverhere.capstone.boss_over_here_backend.domain.recommend.dto.response.RecommendPlanItemResponse;
import com.bossoverhere.capstone.boss_over_here_backend.domain.recommend.service.RecommendService;
import com.bossoverhere.capstone.boss_over_here_backend.global.common.dto.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Recommend", description = "추천 API")
@RestController
@RequestMapping("/api/recommend")
@RequiredArgsConstructor
public class RecommendController {
    private final RecommendService recommendService;

    @Operation(summary="추천 옵션 조회",
            description="문항 + 스팟 목록을 함께 내려줍니다.")
    @GetMapping("/options")
    public ResponseEntity<CommonResponse<RecommendOptionsResponse>> getOptions(
            @AuthenticationPrincipal Long userId
    ) {
        RecommendOptionsResponse opts =
                recommendService.findAllValuePicksAndSpots(userId);
        return ResponseEntity.ok(CommonResponse.createSuccess(opts));
    }

    @Operation(
            summary = "추천 요청",
            description = "문항, 스팟, 날짜·시간을 전달하면 AI 추천 플랜 리스트를 반환합니다."
    )
    @PostMapping
    public ResponseEntity<CommonResponse<List<RecommendPlanItemResponse>>> recommend(
            @RequestBody @Valid RecommendRequest dto,
            @AuthenticationPrincipal Long userId
    ) {
        List<RecommendPlanItemResponse> plan = recommendService.recommend(userId, dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(CommonResponse.createSuccess(plan));
    }


    @Operation(summary = "추천 히스토리 조회", description = "사용자의 모든 추천 내역을 클러스터 정보와 함께 조회합니다.")
    @GetMapping("/history")
    public ResponseEntity<CommonResponse<List<RecommendHistoryResponse>>> getRecommendHistory(
            @AuthenticationPrincipal Long userId
    ) {
        List<RecommendHistoryResponse> history = recommendService.getAllRecommendHistory(userId);
        return ResponseEntity.ok(CommonResponse.createSuccess(history));
    }
}