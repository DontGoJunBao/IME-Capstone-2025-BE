package com.bossoverhere.capstone.boss_over_here_backend.domain.spot.controller;

import com.bossoverhere.capstone.boss_over_here_backend.domain.spot.dto.response.SpotResponse;
import com.bossoverhere.capstone.boss_over_here_backend.domain.spot.service.SpotService;
import com.bossoverhere.capstone.boss_over_here_backend.global.common.dto.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Spot", description = "스팟 정보 API")
@RestController
@RequestMapping("/api/spots")
@RequiredArgsConstructor
public class SpotController {

    private final SpotService spotService;

    /**
     * 모든 스팟 조회
     */
    @Operation(summary = "모든 스팟 조회", description = "모든 스팟 정보를 조회합니다.")
    @GetMapping
    public ResponseEntity<CommonResponse<List<SpotResponse>>> getAllSpots() {
        List<SpotResponse> spots = spotService.findAll().stream()
                .map(SpotResponse::of)
                .collect(Collectors.toList());

        return ResponseEntity.ok(CommonResponse.createSuccess(spots));
    }

    /**
     * 특정 스팟 조회
     */
    @Operation(summary = "스팟 조회", description = "스팟 ID로 특정 스팟의 정보를 조회합니다.")
    @GetMapping("/{spotId}")
    public ResponseEntity<CommonResponse<SpotResponse>> getSpotById(@PathVariable Long spotId) {
        SpotResponse spotResponse = SpotResponse.of(spotService.getSpotById(spotId));
        return ResponseEntity.ok(CommonResponse.createSuccess(spotResponse));
    }
}