package com.bossoverhere.capstone.boss_over_here_backend.domain.spot.service;

import com.bossoverhere.capstone.boss_over_here_backend.domain.spot.dao.SpotRepository;
import com.bossoverhere.capstone.boss_over_here_backend.domain.spot.domain.Spot;
import com.bossoverhere.capstone.boss_over_here_backend.global.error.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SpotService {
    private final SpotRepository spotRepository;
    // 스팟 정보 조회
    public List<Spot> findAll(){
        return spotRepository.findAll();
    }

    public Spot getSpotById(Long spotId) {
        return spotRepository.findById(spotId)
                .orElseThrow(() -> new ApplicationException(SpotErrorCode.NOTFOUND_SPOT));
    }




}