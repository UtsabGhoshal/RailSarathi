package com.railsarathi.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {

    @NotBlank(message = "Email or username is required")
    @JsonAlias({"usernameOrEmail", "emailOrUsername", "username", "email"})
    private String emailOrUsername;

    @NotBlank(message = "Password is required")
    private String password;
}
