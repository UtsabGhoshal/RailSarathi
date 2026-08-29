package com.railsarathi.service;

import com.railsarathi.dto.LiveTrainStatusDto;

public interface TinyFishScraperService {

    LiveTrainStatusDto getLiveTrainStatus(String trainNumber, boolean forceRefresh);

    default LiveTrainStatusDto getLiveTrainStatus(String trainNumber) {
        return getLiveTrainStatus(trainNumber, false);
    }
}
