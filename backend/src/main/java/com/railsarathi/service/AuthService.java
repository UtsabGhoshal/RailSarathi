package com.railsarathi.service;

import com.railsarathi.dto.AuthResponse;
import com.railsarathi.dto.LoginRequest;
import com.railsarathi.dto.RegisterRequest;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}
