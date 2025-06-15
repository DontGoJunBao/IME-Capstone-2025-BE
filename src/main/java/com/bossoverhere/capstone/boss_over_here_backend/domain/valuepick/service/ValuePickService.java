package com.bossoverhere.capstone.boss_over_here_backend.domain.valuepick.service;

import com.bossoverhere.capstone.boss_over_here_backend.domain.valuepick.dao.ValuePickRepository;
import com.bossoverhere.capstone.boss_over_here_backend.domain.valuepick.domain.ValuePick;
import com.bossoverhere.capstone.boss_over_here_backend.global.error.ApplicationException;
import com.bossoverhere.capstone.boss_over_here_backend.global.error.RecommendErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ValuePickService {
    private final ValuePickRepository valuePickRepository;

    public List<ValuePick> getAllValuePicks() {
        return valuePickRepository.findAllValueOrdered();
    }

    public List<ValuePick> getValuePicksByIds(List<Long> ids) {
        return valuePickRepository.findAllById(ids);
    }

    public ValuePick getValuePickById(Long id) {
        return valuePickRepository.findById(id)
                .orElseThrow(() -> new ApplicationException(RecommendErrorCode.VALUE_PICK_NOT_FOUND));
    }
}