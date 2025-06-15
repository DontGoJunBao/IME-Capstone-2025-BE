package com.bossoverhere.capstone.boss_over_here_backend.domain.recommend.domain;

import com.bossoverhere.capstone.boss_over_here_backend.domain.result.domain.Result;
import com.bossoverhere.capstone.boss_over_here_backend.domain.spot.domain.Spot;
import com.bossoverhere.capstone.boss_over_here_backend.domain.user.domain.User;
import com.bossoverhere.capstone.boss_over_here_backend.domain.valuepick.domain.RecommendValuePick;
import com.bossoverhere.capstone.boss_over_here_backend.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Table(name = "recommend")
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Recommend extends BaseEntity {
    @Id
    @Column(name = "recommend_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Embedded
    private BusinessSchedule businessSchedule;

    @OneToMany(mappedBy = "recommend", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<RecommendValuePick> recommendValuePicks = new ArrayList<>();


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "spot_id", nullable = false)
    private Spot spot;

    @OneToMany(mappedBy = "recommend", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Result> results = new ArrayList<>();

    public void addResult(Result result) {
        result.assignRecommend(this);
        this.results.add(result);
    }

    public void confirmUser(User user) {
        this.user = user;
        user.updateRecommend(this);
    }


    /**
     * 기존 RecommendValuePick 전부 지우고, 새 리스트로 교체
     */
    public void replaceValuePicks(List<RecommendValuePick> newPicks) {
        // 1) 기존 것들 전부 unlink (orphanRemoval 으로 삭제)
        this.recommendValuePicks.clear();

        // 2) 새로운 것들 add
        for (RecommendValuePick pick : newPicks) {
            confirmRecommendValuePick(pick);
        }
    }

    public void confirmRecommendValuePick(RecommendValuePick recommendValuePick) {
        recommendValuePicks.add(recommendValuePick);
        recommendValuePick.addRecommend(this);
    }

    public static Recommend createRecommend(User user, List<RecommendValuePick> picks, BusinessSchedule schedule,
                                            Spot spot) {
        Recommend recommend = Recommend.builder().build();
        recommend.confirmUser(user);
        for (RecommendValuePick recommendValuePick : picks) {
            recommend.confirmRecommendValuePick(recommendValuePick);
        }
        recommend.updateSpot(spot);
        recommend.updateBusinessSchedule(schedule);
        return recommend;
    }

    public void updateBusinessSchedule(BusinessSchedule businessSchedule) {
        this.businessSchedule = businessSchedule;
    }

    public void updateSpot(Spot spot) {
        this.spot = spot;
    }

}
