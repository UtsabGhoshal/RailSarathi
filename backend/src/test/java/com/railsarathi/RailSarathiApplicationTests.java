package com.railsarathi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.railsarathi.repository.UserRepository;
import com.railsarathi.entity.User;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;

@SpringBootTest
class RailSarathiApplicationTests {

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldSaveUser() {
        User user = new User();
        user.setFullName("Utsab Ghoshal");
        user.setUsername("utsab");
        user.setEmail("utsab@test.com");
        user.setPassword("temporary-password");
        user.setPhone("9876543210");
        user.setDateOfBirth(LocalDate.of(2004, 1, 1));
        
        User savedUser = userRepository.save(user);
        System.out.println("Generated ID: " + savedUser.getId());
    }
}