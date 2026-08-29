package com.railsarathi.service.impl;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.railsarathi.dto.ClassAvailabilityDto;
import com.railsarathi.dto.StationDto;
import com.railsarathi.dto.TrainScheduleDto;
import com.railsarathi.dto.TrainSearchResultDto;
import com.railsarathi.entity.Coach;
import com.railsarathi.entity.Station;
import com.railsarathi.entity.Train;
import com.railsarathi.entity.TrainSchedule;
import com.railsarathi.enums.CoachClass;
import com.railsarathi.enums.SeatStatus;
import com.railsarathi.exception.ResourceNotFoundException;
import com.railsarathi.repository.CoachRepository;
import com.railsarathi.repository.SeatRepository;
import com.railsarathi.repository.StationRepository;
import com.railsarathi.repository.TrainRepository;
import com.railsarathi.repository.TrainScheduleRepository;
import com.railsarathi.service.TrainSearchService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainSearchServiceImpl implements TrainSearchService {

    private final StationRepository stationRepository;
    private final TrainRepository trainRepository;
    private final TrainScheduleRepository scheduleRepository;
    private final CoachRepository coachRepository;
    private final SeatRepository seatRepository;

    @Override
    @Transactional(readOnly = true)
    public List<TrainSearchResultDto> searchTrains(String sourceCode, String destCode, LocalDate journeyDate) {
        Station sourceStation = stationRepository.findByStationCodeIgnoreCase(sourceCode.trim())
                .orElseThrow(() -> new ResourceNotFoundException("Source station not found with code: " + sourceCode));

        Station destStation = stationRepository.findByStationCodeIgnoreCase(destCode.trim())
                .orElseThrow(() -> new ResourceNotFoundException("Destination station not found with code: " + destCode));

        // Find all trains containing both stations where stopOrder(source) < stopOrder(dest)
        List<Train> matchingTrains = scheduleRepository.findTrainsBetweenStations(sourceStation.getId(), destStation.getId());

        String dayOfWeekCode = journeyDate != null ? journeyDate.getDayOfWeek().name().substring(0, 3) : null;

        List<TrainSearchResultDto> results = new ArrayList<>();

        for (Train train : matchingTrains) {
            // Check day running filter if journeyDate is specified
            if (dayOfWeekCode != null && train.getRunsOnDays() != null) {
                if (!train.getRunsOnDays().toUpperCase().contains(dayOfWeekCode) && !train.getRunsOnDays().equalsIgnoreCase("ALL")) {
                    continue; // Skip trains that do not run on requested day
                }
            }

            List<TrainSchedule> allSchedules = scheduleRepository.findByTrainIdOrderByStopOrderAsc(train.getId());
            TrainSchedule sourceStop = allSchedules.stream()
                    .filter(s -> s.getStation().getId().equals(sourceStation.getId()))
                    .findFirst()
                    .orElse(null);

            TrainSchedule destStop = allSchedules.stream()
                    .filter(s -> s.getStation().getId().equals(destStation.getId()))
                    .findFirst()
                    .orElse(null);

            if (sourceStop != null && destStop != null && sourceStop.getStopOrder() < destStop.getStopOrder()) {
                double travelDistance = Math.max(10.0, destStop.getDistanceFromSourceKm() - sourceStop.getDistanceFromSourceKm());
                LocalTime depTime = sourceStop.getDepartureTime() != null ? sourceStop.getDepartureTime() : sourceStop.getArrivalTime();
                LocalTime arrTime = destStop.getArrivalTime() != null ? destStop.getArrivalTime() : destStop.getDepartureTime();

                int dayDiff = (destStop.getDayNumber() != null && sourceStop.getDayNumber() != null)
                        ? Math.max(0, destStop.getDayNumber() - sourceStop.getDayNumber())
                        : 0;

                String formattedDuration = calculateDuration(depTime, arrTime, dayDiff);

                List<ClassAvailabilityDto> classAvailabilities = calculateClassAvailabilities(train, travelDistance);
                List<TrainScheduleDto> scheduleDtos = allSchedules.stream().map(this::mapToScheduleDto).toList();

                TrainSearchResultDto resultDto = TrainSearchResultDto.builder()
                        .trainId(train.getId())
                        .trainNumber(train.getTrainNumber())
                        .trainName(train.getTrainName())
                        .trainType(train.getTrainType())
                        .originStation(mapToStationDto(train.getSourceStation()))
                        .terminusStation(mapToStationDto(train.getDestinationStation()))
                        .boardingStation(mapToStationDto(sourceStation))
                        .destinationStation(mapToStationDto(destStation))
                        .departureTime(depTime)
                        .arrivalTime(arrTime)
                        .durationFormatted(formattedDuration)
                        .travelDistanceKm(travelDistance)
                        .dayDifference(dayDiff)
                        .runsOnDays(train.getRunsOnDays())
                        .availableClasses(classAvailabilities)
                        .routeSchedule(scheduleDtos)
                        .build();

                results.add(resultDto);
            }
        }

        // Sort by departure time
        results.sort(Comparator.comparing(r -> r.getDepartureTime() != null ? r.getDepartureTime() : LocalTime.MIDNIGHT));
        return results;
    }

    @Override
    @Transactional(readOnly = true)
    public TrainSearchResultDto getTrainDetails(String trainNumber) {
        Train train = trainRepository.findByTrainNumber(trainNumber.trim())
                .orElseThrow(() -> new ResourceNotFoundException("Train not found with number: " + trainNumber));

        List<TrainSchedule> allSchedules = scheduleRepository.findByTrainIdOrderByStopOrderAsc(train.getId());
        double totalDistance = train.getTotalDistanceKm() != null ? train.getTotalDistanceKm() : 500.0;

        TrainSchedule firstStop = allSchedules.isEmpty() ? null : allSchedules.get(0);
        TrainSchedule lastStop = allSchedules.isEmpty() ? null : allSchedules.get(allSchedules.size() - 1);

        LocalTime depTime = firstStop != null && firstStop.getDepartureTime() != null ? firstStop.getDepartureTime() : LocalTime.of(6, 0);
        LocalTime arrTime = lastStop != null && lastStop.getArrivalTime() != null ? lastStop.getArrivalTime() : LocalTime.of(18, 0);

        int dayDiff = (firstStop != null && lastStop != null && firstStop.getDayNumber() != null && lastStop.getDayNumber() != null)
                ? Math.max(0, lastStop.getDayNumber() - firstStop.getDayNumber())
                : 0;

        List<ClassAvailabilityDto> classAvailabilities = calculateClassAvailabilities(train, totalDistance);
        List<TrainScheduleDto> scheduleDtos = allSchedules.stream().map(this::mapToScheduleDto).toList();

        return TrainSearchResultDto.builder()
                .trainId(train.getId())
                .trainNumber(train.getTrainNumber())
                .trainName(train.getTrainName())
                .trainType(train.getTrainType())
                .originStation(mapToStationDto(train.getSourceStation()))
                .terminusStation(mapToStationDto(train.getDestinationStation()))
                .boardingStation(mapToStationDto(train.getSourceStation()))
                .destinationStation(mapToStationDto(train.getDestinationStation()))
                .departureTime(depTime)
                .arrivalTime(arrTime)
                .durationFormatted(calculateDuration(depTime, arrTime, dayDiff))
                .travelDistanceKm(totalDistance)
                .dayDifference(dayDiff)
                .runsOnDays(train.getRunsOnDays())
                .availableClasses(classAvailabilities)
                .routeSchedule(scheduleDtos)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    @org.springframework.cache.annotation.Cacheable(value = "train_schedules", key = "#trainNumber")
    public List<TrainScheduleDto> getTrainSchedule(String trainNumber) {
        Train train = trainRepository.findByTrainNumber(trainNumber.trim())
                .orElseThrow(() -> new ResourceNotFoundException("Train not found with number: " + trainNumber));

        return scheduleRepository.findByTrainIdOrderByStopOrderAsc(train.getId()).stream()
                .map(this::mapToScheduleDto)
                .toList();
    }

    private List<ClassAvailabilityDto> calculateClassAvailabilities(Train train, double distanceKm) {
        List<Coach> coaches = coachRepository.findByTrainId(train.getId());
        Set<CoachClass> distinctClasses = new HashSet<>();
        for (Coach coach : coaches) {
            distinctClasses.add(coach.getCoachClass());
        }

        // If no coaches attached, add default classes based on train type
        if (distinctClasses.isEmpty()) {
            if (train.getTrainType().name().contains("VANDE_BHARAT") || train.getTrainType().name().contains("SHATABDI")) {
                distinctClasses.add(CoachClass.AC_CHAIR_CAR);
                distinctClasses.add(CoachClass.EXECUTIVE_CHAIR_CAR);
            } else {
                distinctClasses.add(CoachClass.SECOND_AC);
                distinctClasses.add(CoachClass.THIRD_AC);
                distinctClasses.add(CoachClass.SLEEPER);
            }
        }

        List<ClassAvailabilityDto> list = new ArrayList<>();
        for (CoachClass coachClass : distinctClasses) {
            double fare = Math.max(60.0, Math.round(distanceKm * coachClass.getBaseRatePerKm()));

            long availableCount = seatRepository.countSeatsByTrainAndClassAndStatus(train.getId(), coachClass, SeatStatus.AVAILABLE);
            int totalSeats = coaches.stream()
                    .filter(c -> c.getCoachClass() == coachClass)
                    .mapToInt(Coach::getTotalSeats)
                    .sum();

            if (totalSeats == 0) {
                totalSeats = 72; // default coach capacity
                availableCount = 42;
            }

            String statusCode = availableCount > 0 ? "AVL" : "RAC";
            String statusDesc = availableCount > 0 ? "AVL - " + availableCount : "RAC - 14";

            list.add(ClassAvailabilityDto.builder()
                    .coachClass(coachClass)
                    .classCode(coachClass.getCode())
                    .className(coachClass.getDisplayName())
                    .fare(fare)
                    .totalSeats(totalSeats)
                    .availableSeats((int) availableCount)
                    .statusCode(statusCode)
                    .statusDescription(statusDesc)
                    .build());
        }

        list.sort(Comparator.comparingDouble(ClassAvailabilityDto::getFare).reversed());
        return list;
    }

    private String calculateDuration(LocalTime depTime, LocalTime arrTime, int dayDiff) {
        if (depTime == null || arrTime == null) {
            return "08h 00m";
        }
        long minutes;
        if (dayDiff == 0) {
            if (arrTime.isAfter(depTime)) {
                minutes = Duration.between(depTime, arrTime).toMinutes();
            } else {
                minutes = Duration.between(depTime, arrTime.plusHours(24)).toMinutes();
            }
        } else {
            minutes = Duration.between(depTime, arrTime.plusHours(24L * dayDiff)).toMinutes();
        }

        long hours = minutes / 60;
        long remainingMins = minutes % 60;
        return String.format("%02dh %02dm", hours, remainingMins);
    }

    private StationDto mapToStationDto(Station station) {
        if (station == null) return null;
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

    private TrainScheduleDto mapToScheduleDto(TrainSchedule schedule) {
        return TrainScheduleDto.builder()
                .stopOrder(schedule.getStopOrder())
                .stationCode(schedule.getStation().getStationCode())
                .stationName(schedule.getStation().getStationName())
                .city(schedule.getStation().getCity())
                .state(schedule.getStation().getState())
                .arrivalTime(schedule.getArrivalTime())
                .departureTime(schedule.getDepartureTime())
                .haltMinutes(schedule.getHaltMinutes())
                .distanceFromSourceKm(schedule.getDistanceFromSourceKm())
                .dayNumber(schedule.getDayNumber())
                .platform(schedule.getPlatform())
                .build();
    }
}
