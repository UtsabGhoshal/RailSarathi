package com.railsarathi.dto;

import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;

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
public class TrainScheduleDto {

    private Integer stopOrder;
    private String stationCode;
    private String stationName;
    private String city;
    private String state;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime arrivalTime;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime departureTime;

    private Integer haltMinutes;
    private Double distanceFromSourceKm;
    private Integer dayNumber;
    private String platform;
}
