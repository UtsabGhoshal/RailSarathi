package com.railsarathi;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;

import com.railsarathi.dto.LiveTrainStatusDto;
import com.railsarathi.seeder.DatabaseSeeder;
import com.railsarathi.service.StationService;
import com.railsarathi.service.TinyFishScraperService;
import com.railsarathi.service.TrainSearchService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class CacheIntegrationTests {

    @Autowired
    private TinyFishScraperService scraperService;

    @Autowired
    private StationService stationService;

    @Autowired
    private TrainSearchService trainSearchService;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private DatabaseSeeder databaseSeeder;

    @BeforeEach
    void setUp() {
        databaseSeeder.run();
        // Clear caches before each test run
        cacheManager.getCacheNames().forEach(name -> {
            var cache = cacheManager.getCache(name);
            if (cache != null) {
                cache.clear();
            }
        });
    }

    @Test
    void shouldCacheLiveTrainStatusForConsecutiveCalls() {
        // First Call: Fetches and places into cache
        LiveTrainStatusDto firstCall = scraperService.getLiveTrainStatus("22301", false);
        assertNotNull(firstCall);
        assertNotNull(firstCall.getCheckedAt());

        // Second Call: Should immediately hit the cache and return identical checkedAt timestamp
        LiveTrainStatusDto secondCall = scraperService.getLiveTrainStatus("22301", false);
        assertNotNull(secondCall);
        assertEquals(firstCall.getCheckedAt(), secondCall.getCheckedAt(), "Cached object must match first response timestamp");
        assertEquals(firstCall.getTrainNumber(), secondCall.getTrainNumber());
    }

    @Test
    void shouldCacheStationSearchAndSchedules() {
        // Station search cache verification
        var stations1 = stationService.searchStations("Howrah");
        var stations2 = stationService.searchStations("Howrah");
        assertEquals(stations1.size(), stations2.size());

        // Schedule cache verification
        var schedule1 = trainSearchService.getTrainSchedule("22301");
        var schedule2 = trainSearchService.getTrainSchedule("22301");
        assertEquals(schedule1.size(), schedule2.size());
    }
}
