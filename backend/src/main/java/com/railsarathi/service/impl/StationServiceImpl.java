package com.railsarathi.service.impl;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.railsarathi.dto.StationDto;
import com.railsarathi.entity.Station;
import com.railsarathi.exception.ResourceNotFoundException;
import com.railsarathi.repository.StationRepository;
import com.railsarathi.service.StationService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StationServiceImpl implements StationService {

    private final StationRepository stationRepository;

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "stations", key = "'all'")
    public List<StationDto> getAllStations() {
        return stationRepository.findAll().stream()
                .map(this::mapToStationDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "stations", key = "'search:' + #query")
    public List<StationDto> searchStations(String query) {
        if (query == null || query.trim().isBlank()) {
            return getAllStations();
        }
        return stationRepository.searchStations(query.trim()).stream()
                .map(this::mapToStationDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public StationDto getStationByCode(String stationCode) {
        Station station = stationRepository.findByStationCodeIgnoreCase(stationCode.trim())
                .orElseThrow(() -> new ResourceNotFoundException("Station not found with code: " + stationCode));
        return mapToStationDto(station);
    }

    @Override
    @Transactional(readOnly = true)
    public StationDto getStationById(Long id) {
        Station station = stationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Station not found with ID: " + id));
        return mapToStationDto(station);
    }

    private StationDto mapToStationDto(Station station) {
        return StationDto.builder()
                .id(station.getId())
                .stationCode(station.getStationCode())
                .stationName(station.getStationName())
                .city(station.getCity())
                .state(station.getState())
                .zone(station.getZone())
                .totalPlatforms(station.getTotalPlatforms())
                .build();
    }
}
