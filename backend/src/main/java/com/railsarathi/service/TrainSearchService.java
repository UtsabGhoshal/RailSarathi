package com.railsarathi.service;

import java.time.LocalDate;
import java.util.List;

import com.railsarathi.dto.TrainScheduleDto;
import com.railsarathi.dto.TrainSearchResultDto;

public interface TrainSearchService {

    List<TrainSearchResultDto> searchTrains(String sourceCode, String destCode, LocalDate journeyDate);

    TrainSearchResultDto getTrainDetails(String trainNumber);

    List<TrainScheduleDto> getTrainSchedule(String trainNumber);
}
