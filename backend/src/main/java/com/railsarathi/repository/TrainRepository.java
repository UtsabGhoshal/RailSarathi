package com.railsarathi.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.railsarathi.entity.Train;

@Repository
public interface TrainRepository extends JpaRepository<Train, Long> {

    Optional<Train> findByTrainNumber(String trainNumber);

    boolean existsByTrainNumber(String trainNumber);

    @Query("SELECT t FROM Train t WHERE " +
           "LOWER(t.trainNumber) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(t.trainName) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Train> searchTrains(@Param("query") String query);
}
