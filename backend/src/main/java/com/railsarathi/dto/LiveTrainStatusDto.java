package com.railsarathi.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
public class LiveTrainStatusDto {

    private String trainNumber;
    private String trainName;
    private String currentStation;
    private String nextStation;
    private Integer delayMinutes;
    private String statusMessage;
    private String expectedArrival;
    private String dataSource;
    
    @Builder.Default
    private LocalDateTime checkedAt = LocalDateTime.now();

    @Builder.Default
    private List<String> rawInsights = new ArrayList<>();
}
