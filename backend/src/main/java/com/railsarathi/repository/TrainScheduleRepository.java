package com.railsarathi.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.railsarathi.entity.Train;
import com.railsarathi.entity.TrainSchedule;

@Repository
public interface TrainScheduleRepository extends JpaRepository<TrainSchedule, Long> {

    List<TrainSchedule> findByTrainIdOrderByStopOrderAsc(Long trainId);

    List<TrainSchedule> findByTrainTrainNumberOrderByStopOrderAsc(String trainNumber);

    Optional<TrainSchedule> findByTrainIdAndStationId(Long trainId, Long stationId);

    @Query("SELECT DISTINCT s1.train FROM TrainSchedule s1 " +
           "JOIN TrainSchedule s2 ON s1.train.id = s2.train.id " +
           "WHERE s1.station.id = :sourceStationId " +
           "AND s2.station.id = :destStationId " +
           "AND s1.stopOrder < s2.stopOrder")
    List<Train> findTrainsBetweenStations(
            @Param("sourceStationId") Long sourceStationId,
            @Param("destStationId") Long destStationId
    );
}
