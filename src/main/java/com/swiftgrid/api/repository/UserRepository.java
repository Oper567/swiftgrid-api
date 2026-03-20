package com.swiftgrid.api.repository;

import com.swiftgrid.api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Spring Boot is smart enough to write the SQL query automatically just from this method name!
    // Translates to: SELECT * FROM users WHERE email = ?
    Optional<User> findByEmail(String email);
    
    boolean existsByEmail(String email);

    
}