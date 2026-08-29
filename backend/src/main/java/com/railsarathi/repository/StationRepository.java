package com.railsarathi.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.railsarathi.entity.Station;

@Repository
public interface StationRepository extends JpaRepository<Station, Long> {

    Optional<Station> findByStationCodeIgnoreCase(String stationCode);

    boolean existsByStationCodeIgnoreCase(String stationCode);

    @Query("SELECT s FROM Station s WHERE " +
           "LOWER(s.stationCode) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(s.stationName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(s.city) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "ORDER BY s.stationName ASC")
    List<Station> searchStations(@Param("query") String query);
}
