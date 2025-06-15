package com.bossoverhere.capstone.boss_over_here_backend.domain.result.domain;

import com.bossoverhere.capstone.boss_over_here_backend.domain.recommend.domain.Recommend;
import com.bossoverhere.capstone.boss_over_here_backend.domain.spot.domain.Spot;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

@Entity
@Table(name = "result")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Result {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "result_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recommend_id", nullable = false)
    private Recommend recommend;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_spot_id", nullable = false)
    private Spot fromSpot;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_spot_id", nullable = false)
    private Spot toSpot;

    @Column(nullable = false)
    private LocalTime time;

    public static Result create(LocalTime time, Spot from, Spot to) {
        return Result.builder()
                .time(time)
                .fromSpot(from)
                .toSpot(to)
                .build();
    }

    public void assignRecommend(Recommend recommend) {
        this.recommend = recommend;
    }
}
