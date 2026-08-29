package com.railsarathi.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.railsarathi.dto.ApiResponse;
import com.railsarathi.dto.UserProfileDto;
import com.railsarathi.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileDto>> getCurrentUserProfile() {
        UserProfileDto profile = userService.getCurrentUserProfile();
        return ResponseEntity.ok(
                ApiResponse.success("User profile retrieved successfully.", profile)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserProfileDto>> getUserById(@PathVariable Long id) {
        UserProfileDto profile = userService.getUserById(id);
        return ResponseEntity.ok(
                ApiResponse.success("User retrieved successfully.", profile)
        );
    }
}
