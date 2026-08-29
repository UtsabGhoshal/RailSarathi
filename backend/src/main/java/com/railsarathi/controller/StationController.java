package com.railsarathi.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.railsarathi.dto.ApiResponse;
import com.railsarathi.dto.StationDto;
import com.railsarathi.service.StationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/stations")
@RequiredArgsConstructor
public class StationController {

    private final StationService stationService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<StationDto>>> getAllStations() {
        List<StationDto> stations = stationService.getAllStations();
        return ResponseEntity.ok(
                ApiResponse.success("Stations retrieved successfully.", stations)
        );
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<StationDto>>> searchStations(@RequestParam(required = false) String query) {
        List<StationDto> stations = stationService.searchStations(query);
        return ResponseEntity.ok(
                ApiResponse.success("Matching stations retrieved.", stations)
        );
    }

    @GetMapping("/{stationCode}")
    public ResponseEntity<ApiResponse<StationDto>> getStationByCode(@PathVariable String stationCode) {
        StationDto station = stationService.getStationByCode(stationCode);
        return ResponseEntity.ok(
                ApiResponse.success("Station details retrieved.", station)
        );
    }
}
