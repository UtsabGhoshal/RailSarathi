package com.railsarathi.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.railsarathi.enums.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

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
public class RegisterRequest {

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    private String fullName;

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 30, message = "Username must be between 3 and 30 characters")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;

    @JsonAlias({"phoneNumber", "phone"})
    @Pattern(regexp = "^(\\+?91)?[0-9]{10}$|^$", message = "Phone number must be a valid 10-digit number (e.g. 9876543210 or +919876543210)")
    private String phone;

    private LocalDate dateOfBirth;

    private Role role;
}
