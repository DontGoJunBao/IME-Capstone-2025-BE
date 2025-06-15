package com.bossoverhere.capstone.boss_over_here_backend.domain.spot.dto.response;

import com.bossoverhere.capstone.boss_over_here_backend.domain.spot.domain.Spot;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SpotResponse {
    private Long id;
    private String name;
    private String address;
    private double latitude;
    private double longitude;

    public static SpotResponse of(Spot spot) {
        return SpotResponse.builder()
                .id(spot.getId())
                .name(spot.getName())
                .address(spot.getAddress())
                .latitude(spot.getLatitude())
                .longitude(spot.getLongitude())
                .build();
    }
}