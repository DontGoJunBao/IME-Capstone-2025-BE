package com.bossoverhere.capstone.boss_over_here_backend.domain.user.domain;

import com.bossoverhere.capstone.boss_over_here_backend.domain.recommend.domain.Recommend;
import com.bossoverhere.capstone.boss_over_here_backend.domain.record.domain.Record;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@Table(name = "USERS")
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "OAUTH_ID",nullable = false)
    private String oauthId;
    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Recommend> recommends = new ArrayList<>();

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Record> record = new ArrayList<>();

    public void updateRecommend(Recommend recommend) {
        this.recommends.add(recommend);
    }



}