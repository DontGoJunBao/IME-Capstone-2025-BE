package com.bossoverhere.capstone.boss_over_here_backend.global.config;

import com.bossoverhere.capstone.boss_over_here_backend.domain.spot.dao.SpotRepository;
import com.bossoverhere.capstone.boss_over_here_backend.domain.spot.domain.Spot;
import com.bossoverhere.capstone.boss_over_here_backend.domain.spot.dto.request.SpotCsv;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SpotInitializer {

    private final SpotRepository spotRepository;

    @PostConstruct
    public void init() throws Exception {
        if (spotRepository.count() > 0) return;

        try (var reader = new InputStreamReader(
                new ClassPathResource("spots.csv").getInputStream(),
                StandardCharsets.UTF_8)) {

            CsvToBean<SpotCsv> csvToBean = new CsvToBeanBuilder<SpotCsv>(reader)
                    .withType(SpotCsv.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            List<SpotCsv> list = csvToBean.parse();

            List<Spot> spots = list.stream()
                    .map(c -> Spot.builder()
                            .name(c.getName())
                            .address(c.getAddress())
                            .latitude(c.getLatitude())
                            .longitude(c.getLongitude())
                            .build())
                    .toList();

            spotRepository.saveAll(spots);
        }
    }
}