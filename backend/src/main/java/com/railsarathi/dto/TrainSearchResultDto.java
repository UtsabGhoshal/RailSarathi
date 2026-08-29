package com.railsarathi.dto;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.railsarathi.enums.TrainType;

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
public class TrainSearchResultDto {

    private Long trainId;
    private String trainNumber;
    private String trainName;
    private TrainType trainType;

    private StationDto originStation;
    private StationDto terminusStation;

    private StationDto boardingStation;
    private StationDto destinationStation;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime departureTime;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime arrivalTime;

    private String durationFormatted;
    private Double travelDistanceKm;
    private Integer dayDifference;
    private String runsOnDays;

    @Builder.Default
    private List<ClassAvailabilityDto> availableClasses = new ArrayList<>();

    @Builder.Default
    private List<TrainScheduleDto> routeSchedule = new ArrayList<>();
}
