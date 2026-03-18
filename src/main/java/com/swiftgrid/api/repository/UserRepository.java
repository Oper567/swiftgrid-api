package com.swiftgrid.api.repository;

import com.swiftgrid.api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    // This magically writes the SQL query to find a user by their email
    Optional<User> findByEmail(String email);
}