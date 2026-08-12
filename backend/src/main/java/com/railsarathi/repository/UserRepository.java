package com.railsarathi.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.railsarathi.entity.User;
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
}
