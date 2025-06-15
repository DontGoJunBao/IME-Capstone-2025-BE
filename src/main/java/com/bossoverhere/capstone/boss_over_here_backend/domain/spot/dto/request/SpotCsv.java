package com.bossoverhere.capstone.boss_over_here_backend.domain.spot.dto.request;

import com.opencsv.bean.CsvBindByName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpotCsv {

    @CsvBindByName(column = "name")
    private String name;

    @CsvBindByName(column = "address")
    private String address;

    @CsvBindByName(column = "latitude")
    private double latitude;

    @CsvBindByName(column = "longitude")
    private double longitude;
}