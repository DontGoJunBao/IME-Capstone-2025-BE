package com.bossoverhere.capstone.boss_over_here_backend.domain.recommend.dao;

import com.bossoverhere.capstone.boss_over_here_backend.domain.recommend.domain.Recommend;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecommendRepository extends JpaRepository<Recommend, Long> {
}