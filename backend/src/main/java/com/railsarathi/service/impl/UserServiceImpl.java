package com.railsarathi.service.impl;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.railsarathi.dto.UserProfileDto;
import com.railsarathi.entity.User;
import com.railsarathi.exception.ResourceNotFoundException;
import com.railsarathi.exception.UnauthorizedException;
import com.railsarathi.repository.UserRepository;
import com.railsarathi.security.CustomUserDetails;
import com.railsarathi.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserProfileDto getCurrentUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
            throw new UnauthorizedException("User is not authenticated");
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userRepository.findById(userDetails.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userDetails.getUserId()));

        return mapToUserProfileDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));

        return mapToUserProfileDto(user);
    }

    private UserProfileDto mapToUserProfileDto(User user) {
        return UserProfileDto.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .dateOfBirth(user.getDateOfBirth())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
