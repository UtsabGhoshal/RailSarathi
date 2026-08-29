package com.railsarathi.config;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.benmanes.caffeine.cache.Caffeine;

import java.util.List;

@Configuration
@EnableCaching
public class CacheConfig {

    @Value("${app.cache.live-status-ttl-seconds:180}")
    private long liveStatusTtlSeconds;

    @Value("${app.cache.schedule-ttl-minutes:30}")
    private long scheduleTtlMinutes;

    @Value("${app.cache.station-ttl-minutes:60}")
    private long stationTtlMinutes;

    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();

        // 1. Live Train Status Cache (3 minutes / 180s TTL)
        CaffeineCache liveStatusCache = new CaffeineCache(
                "train_live_status",
                Caffeine.newBuilder()
                        .expireAfterWrite(liveStatusTtlSeconds, TimeUnit.SECONDS)
                        .maximumSize(1000)
                        .recordStats()
                        .build()
        );

        // 2. Train Schedules Cache (30 minutes TTL)
        CaffeineCache schedulesCache = new CaffeineCache(
                "train_schedules",
                Caffeine.newBuilder()
                        .expireAfterWrite(scheduleTtlMinutes, TimeUnit.MINUTES)
                        .maximumSize(500)
                        .recordStats()
                        .build()
        );

        // 3. Station List & Autocomplete Cache (60 minutes TTL)
        CaffeineCache stationsCache = new CaffeineCache(
                "stations",
                Caffeine.newBuilder()
                        .expireAfterWrite(stationTtlMinutes, TimeUnit.MINUTES)
                        .maximumSize(200)
                        .recordStats()
                        .build()
        );

        cacheManager.setCaches(List.of(liveStatusCache, schedulesCache, stationsCache));
        return cacheManager;
    }
}
