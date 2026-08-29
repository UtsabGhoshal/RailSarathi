package com.railsarathi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.railsarathi.entity.Coach;
import com.railsarathi.enums.CoachClass;

@Repository
public interface CoachRepository extends JpaRepository<Coach, Long> {

    List<Coach> findByTrainId(Long trainId);

    List<Coach> findByTrainTrainNumber(String trainNumber);

    List<Coach> findByTrainIdAndCoachClass(Long trainId, CoachClass coachClass);
}
