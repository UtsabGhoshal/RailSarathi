package com.railsarathi.dto;

import com.railsarathi.enums.CoachClass;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassAvailabilityDto {

    private CoachClass coachClass;
    private String classCode;
    private String className;
    private Double fare;
    private Integer totalSeats;
    private Integer availableSeats;
    private String statusCode; // "AVL", "RAC", "WL"
    private String statusDescription; // "AVL - 42", "RAC - 12", "WL - 8"
}
