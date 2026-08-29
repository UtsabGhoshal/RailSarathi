package com.railsarathi.service.impl;

import java.util.UUID;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.railsarathi.dto.AuthResponse;
import com.railsarathi.dto.LoginRequest;
import com.railsarathi.dto.RegisterRequest;
import com.railsarathi.dto.UserProfileDto;
import com.railsarathi.entity.User;
import com.railsarathi.enums.Role;
import com.railsarathi.exception.ResourceNotFoundException;
import com.railsarathi.exception.UserAlreadyExistsException;
import com.railsarathi.repository.UserRepository;
import com.railsarathi.security.CustomUserDetails;
import com.railsarathi.security.JwtTokenProvider;
import com.railsarathi.service.AuthService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email is already registered: " + request.getEmail());
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("Username is already taken: " + request.getUsername());
        }

        String sessionId = UUID.randomUUID().toString();

        User user = User.builder()
                .fullName(request.getFullName().trim())
                .username(request.getUsername().trim())
                .email(request.getEmail().trim().toLowerCase())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone().trim())
                .dateOfBirth(request.getDateOfBirth())
                .role(Role.ROLE_PASSENGER)
                .activeSessionId(sessionId)
                .build();

        User savedUser = userRepository.save(user);
        log.info("Registered new user with ID: {} and username: {}", savedUser.getId(), savedUser.getUsername());

        String accessToken = jwtTokenProvider.generateToken(savedUser, sessionId);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .tokenType("Bearer")
                .sessionId(sessionId)
                .expiresIn(jwtTokenProvider.getExpirationInMs() / 1000)
                .user(mapToUserProfileDto(savedUser))
                .build();
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        String identifier = request.getEmailOrUsername().trim();
        if (identifier.contains("@")) {
            identifier = identifier.toLowerCase();
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(identifier, request.getPassword())
        );

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userRepository.findById(userDetails.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String sessionId = UUID.randomUUID().toString();
        user.setActiveSessionId(sessionId);
        userRepository.save(user);

        log.info("User {} logged in successfully with session ID: {}", user.getUsername(), sessionId);

        String accessToken = jwtTokenProvider.generateToken(user, sessionId);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .tokenType("Bearer")
                .sessionId(sessionId)
                .expiresIn(jwtTokenProvider.getExpirationInMs() / 1000)
                .user(mapToUserProfileDto(user))
                .build();
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
