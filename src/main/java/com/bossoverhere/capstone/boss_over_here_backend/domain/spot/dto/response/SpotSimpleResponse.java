package com.bossoverhere.capstone.boss_over_here_backend.domain.spot.dto.response;

import com.bossoverhere.capstone.boss_over_here_backend.domain.spot.domain.Spot;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SpotSimpleResponse {
    private Long id;
    private String name;

    public static SpotSimpleResponse of(Spot spot) {
        return SpotSimpleResponse.builder()
                .id(spot.getId())
                .name(spot.getName())
                .build();
    }
}