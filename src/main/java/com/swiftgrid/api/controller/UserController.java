package com.swiftgrid.api.controller;

import com.swiftgrid.api.model.User;
import com.swiftgrid.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User newUser) {
        // Quick check: Does this email already exist?
        if (userRepository.findByEmail(newUser.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Error: Email already in use!");
        }

        // Save the user to the PostgreSQL vault
        userRepository.save(newUser);
        return ResponseEntity.ok("Success: " + newUser.getRole() + " registered successfully!");
    }
}