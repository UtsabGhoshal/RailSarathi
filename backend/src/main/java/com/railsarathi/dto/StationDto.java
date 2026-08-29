package com.railsarathi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StationDto {

    private Long id;
    private String stationCode;
    private String stationName;
    private String city;
    private String state;
    private String zone;
    private Integer totalPlatforms;
}
