package com.bossoverhere.capstone.boss_over_here_backend.domain.valuepick.domain;

import com.bossoverhere.capstone.boss_over_here_backend.domain.recommend.domain.Recommend;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import java.util.List;

@Entity
@Table(name = "recommend_value_pick")
@Getter
@NoArgsConstructor
public class RecommendValuePick {
    @Id
    @Column(name = "recommend_value_pick_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recommend_id", nullable = false)
    private Recommend recommend;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "value_pick_id", nullable = false)
    private ValuePick valuePick;

    @Type(JsonType.class)
    @Column(name = "selected_answers", columnDefinition = "json", nullable = false)
    private List<Integer> selectedAnswers;



    @Builder
    public RecommendValuePick(ValuePick valuePick, List<Integer> selectedAnswers) {
        this.valuePick = valuePick;
        this.selectedAnswers = selectedAnswers;
    }

    public void addRecommend(Recommend recommend) {
        this.recommend = recommend;
    }


}