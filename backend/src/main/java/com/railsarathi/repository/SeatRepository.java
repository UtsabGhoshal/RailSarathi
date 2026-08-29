package com.railsarathi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.railsarathi.entity.Seat;
import com.railsarathi.enums.CoachClass;
import com.railsarathi.enums.SeatStatus;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {

    List<Seat> findByCoachId(Long coachId);

    long countByCoachIdAndSeatStatus(Long coachId, SeatStatus seatStatus);

    @Query("SELECT COUNT(s) FROM Seat s " +
           "WHERE s.coach.train.id = :trainId " +
           "AND s.coach.coachClass = :coachClass " +
           "AND s.seatStatus = :status")
    long countSeatsByTrainAndClassAndStatus(
            @Param("trainId") Long trainId,
            @Param("coachClass") CoachClass coachClass,
            @Param("status") SeatStatus status
    );
}
