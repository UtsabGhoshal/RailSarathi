package com.railsarathi.service;

import com.railsarathi.dto.UserProfileDto;

public interface UserService {

    UserProfileDto getCurrentUserProfile();

    UserProfileDto getUserById(Long id);
}
