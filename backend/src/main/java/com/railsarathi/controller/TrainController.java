package com.railsarathi.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.railsarathi.dto.ApiResponse;
import com.railsarathi.dto.LiveTrainStatusDto;
import com.railsarathi.dto.TrainScheduleDto;
import com.railsarathi.dto.TrainSearchResultDto;
import com.railsarathi.service.TinyFishScraperService;
import com.railsarathi.service.TrainSearchService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/trains")
@RequiredArgsConstructor
public class TrainController {

    private final TrainSearchService trainSearchService;
    private final TinyFishScraperService tinyFishScraperService;

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<TrainSearchResultDto>>> searchTrains(
            @RequestParam String source,
            @RequestParam String destination,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate journeyDate) {

        List<TrainSearchResultDto> trains = trainSearchService.searchTrains(source, destination, journeyDate);
        return ResponseEntity.ok(
                ApiResponse.success("Trains retrieved successfully.", trains)
        );
    }

    @GetMapping("/{trainNumber}")
    public ResponseEntity<ApiResponse<TrainSearchResultDto>> getTrainDetails(@PathVariable String trainNumber) {
        TrainSearchResultDto train = trainSearchService.getTrainDetails(trainNumber);
        return ResponseEntity.ok(
                ApiResponse.success("Train details retrieved.", train)
        );
    }

    @GetMapping("/{trainNumber}/schedule")
    public ResponseEntity<ApiResponse<List<TrainScheduleDto>>> getTrainSchedule(@PathVariable String trainNumber) {
        List<TrainScheduleDto> schedule = trainSearchService.getTrainSchedule(trainNumber);
        return ResponseEntity.ok(
                ApiResponse.success("Train schedule retrieved.", schedule)
        );
    }

    @GetMapping("/{trainNumber}/live-status")
    public ResponseEntity<ApiResponse<LiveTrainStatusDto>> getLiveTrainStatus(
            @PathVariable String trainNumber,
            @RequestParam(required = false, defaultValue = "false") boolean forceRefresh) {
        LiveTrainStatusDto liveStatus = tinyFishScraperService.getLiveTrainStatus(trainNumber, forceRefresh);
        return ResponseEntity.ok(
                ApiResponse.success("Live train status retrieved.", liveStatus)
        );
    }
}
