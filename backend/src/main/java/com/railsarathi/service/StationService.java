package com.railsarathi.service;

import java.util.List;

import com.railsarathi.dto.StationDto;

public interface StationService {

    List<StationDto> getAllStations();

    List<StationDto> searchStations(String query);

    StationDto getStationByCode(String stationCode);

    StationDto getStationById(Long id);
}
