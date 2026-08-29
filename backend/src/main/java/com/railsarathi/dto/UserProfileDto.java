package com.railsarathi.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.railsarathi.enums.Role;

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
public class UserProfileDto {

    private Long id;
    private String fullName;
    private String username;
    private String email;
    private String phone;
    private LocalDate dateOfBirth;
    private Role role;
    private LocalDateTime createdAt;
}
