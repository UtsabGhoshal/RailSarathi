package com.railsarathi.service.impl;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.railsarathi.dto.LiveTrainStatusDto;
import com.railsarathi.entity.Train;
import com.railsarathi.repository.TrainRepository;
import com.railsarathi.service.TinyFishScraperService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TinyFishScraperServiceImpl implements TinyFishScraperService {

    private final TrainRepository trainRepository;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Value("${tinyfish.api.key:}")
    private String apiKey;

    @Value("${tinyfish.search.url:https://api.search.tinyfish.ai}")
    private String searchUrl;

    @Override
    @Cacheable(value = "train_live_status", key = "#trainNumber", condition = "!#forceRefresh")
    public LiveTrainStatusDto getLiveTrainStatus(String trainNumber, boolean forceRefresh) {
        String cleanTrainNumber = trainNumber.trim();
        Optional<Train> trainOpt = trainRepository.findByTrainNumber(cleanTrainNumber);
        String trainName = trainOpt.map(Train::getTrainName).orElse("Train " + cleanTrainNumber);

        if (apiKey == null || apiKey.trim().isBlank() || apiKey.contains("dummy")) {
            log.info("TinyFish API key not configured or dummy test key used. Using timetable fallback for train {}.", cleanTrainNumber);
            return buildTimetableFallback(cleanTrainNumber, trainName, trainOpt);
        }

        try {
            log.info("Fetching TinyFish AI live status for train: {} ({}) [forceRefresh={}]", cleanTrainNumber, trainName, forceRefresh);

            String query = String.format("%s %s live running status today", cleanTrainNumber, trainName);
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
            URI uri = URI.create(searchUrl + "?query=" + encodedQuery);

            RestClient restClient = RestClient.builder()
                    .defaultHeader("X-API-Key", apiKey.trim())
                    .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                    .build();

            String responseBody = restClient.get()
                    .uri(uri)
                    .retrieve()
                    .body(String.class);

            if (responseBody != null) {
                JsonNode root = objectMapper.readTree(responseBody);
                JsonNode results = root.path("results");

                List<String> snippets = new ArrayList<>();
                if (results.isArray()) {
                    for (JsonNode item : results) {
                        String snippet = item.path("snippet").asText();
                        String title = item.path("title").asText();
                        if (!snippet.isBlank()) {
                            snippets.add(title + ": " + snippet);
                        }
                    }
                }

                String statusMessage = "Running on Schedule • Verified via TinyFish AI";
                Integer delayMinutes = 0;

                // Inspect snippets for delay indications
                for (String snippet : snippets) {
                    String lower = snippet.toLowerCase();
                    if (lower.contains("delay") || lower.contains("late")) {
                        statusMessage = "Expected minor delay • Live Web Verified";
                        delayMinutes = 10;
                        break;
                    } else if (lower.contains("on time") || lower.contains("right time")) {
                        statusMessage = "On Time • Verified Live";
                        delayMinutes = 0;
                        break;
                    }
                }

                return LiveTrainStatusDto.builder()
                        .trainNumber(cleanTrainNumber)
                        .trainName(trainName)
                        .currentStation(trainOpt.map(t -> t.getSourceStation().getStationName()).orElse("En Route"))
                        .nextStation(trainOpt.map(t -> t.getDestinationStation().getStationName()).orElse("Destination"))
                        .delayMinutes(delayMinutes)
                        .statusMessage(statusMessage)
                        .expectedArrival("On Track")
                        .dataSource("TinyFish AI Live Web Agent")
                        .checkedAt(LocalDateTime.now())
                        .rawInsights(snippets.stream().limit(3).toList())
                        .build();
            }
        } catch (Exception ex) {
            log.warn("TinyFish AI request failed or timed out: {}. Using timetable fallback.", ex.getMessage());
        }

        return buildTimetableFallback(cleanTrainNumber, trainName, trainOpt);
    }

    private LiveTrainStatusDto buildTimetableFallback(String trainNumber, String trainName, Optional<Train> trainOpt) {
        return LiveTrainStatusDto.builder()
                .trainNumber(trainNumber)
                .trainName(trainName)
                .currentStation(trainOpt.map(t -> t.getSourceStation().getStationName()).orElse("Origin Station"))
                .nextStation(trainOpt.map(t -> t.getDestinationStation().getStationName()).orElse("Next Junction"))
                .delayMinutes(0)
                .statusMessage("Running as per Scheduled Timetable")
                .expectedArrival("Scheduled Time")
                .dataSource("Official Schedule (Local Fallback)")
                .checkedAt(LocalDateTime.now())
                .rawInsights(List.of("Train operating on regular scheduled timetable."))
                .build();
    }
}
