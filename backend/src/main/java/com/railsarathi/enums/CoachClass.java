package com.railsarathi.enums;

import lombok.Getter;

@Getter
public enum CoachClass {
    FIRST_AC("1A", "First AC", 3.20),
    SECOND_AC("2A", "2 Tier AC", 2.10),
    THIRD_AC("3A", "3 Tier AC", 1.40),
    THIRD_AC_ECONOMY("3E", "3 AC Economy", 1.25),
    AC_CHAIR_CAR("CC", "AC Chair Car", 1.30),
    EXECUTIVE_CHAIR_CAR("EC", "Exec. Chair Car", 2.80),
    SLEEPER("SL", "Sleeper Class", 0.55),
    SECOND_SITTING("2S", "Second Sitting", 0.35);

    private final String code;
    private final String displayName;
    private final double baseRatePerKm;

    CoachClass(String code, String displayName, double baseRatePerKm) {
        this.code = code;
        this.displayName = displayName;
        this.baseRatePerKm = baseRatePerKm;
    }
}
