package com.bossoverhere.capstone.boss_over_here_backend.domain.spot.dao;

import com.bossoverhere.capstone.boss_over_here_backend.domain.spot.domain.Spot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpotRepository extends JpaRepository<Spot, Long> {

}
