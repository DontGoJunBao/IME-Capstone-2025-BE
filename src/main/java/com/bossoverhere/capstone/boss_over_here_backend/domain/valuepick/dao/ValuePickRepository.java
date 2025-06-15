package com.bossoverhere.capstone.boss_over_here_backend.domain.valuepick.dao;

import com.bossoverhere.capstone.boss_over_here_backend.domain.valuepick.domain.ValuePick;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ValuePickRepository extends JpaRepository<ValuePick, Long> {
    @Query("SELECT v FROM ValuePick v ORDER BY v.id ASC")
    List<ValuePick> findAllValueOrdered();

}
