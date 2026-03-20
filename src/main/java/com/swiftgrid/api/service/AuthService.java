package com.swiftgrid.api.service;

import com.swiftgrid.api.model.User;
import com.swiftgrid.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder; // Better to use the Interface
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // Injected from SecurityConfig

    // ==========================================
    // 1. REGISTRATION (With BCrypt Hashing)
    // ==========================================
    @Transactional
    public User register(User user) {
        // Validation: Check if email already exists
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email is already registered. Try logging in.");
        }

        // 🔥 SECURITY: Encrypt the password before it ever touches Supabase
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Standard defaults for new signups
        user.setKycStatus(User.KycStatus.PENDING);

        return userRepository.save(user);
    }

    // ==========================================
    // 2. LOGIN (Secure Comparison)
    // ==========================================
    public User login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid email or password."));

        // 🔥 SECURITY: You can't "decrypt" BCrypt. You can only compare matches.
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid email or password.");
        }

        return user;
    }

    // ==========================================
    // 3. KYC DOCUMENT SUBMISSION
    // ==========================================
    @Transactional
    public User submitKycDocuments(Long userId, String idDocumentUrl, String businessReg, String plateNumber) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found."));

        // 1. Every professional user needs a valid ID Card URL
        if (idDocumentUrl != null && !idDocumentUrl.trim().isEmpty()) {
            user.setIdDocumentUrl(idDocumentUrl);
        } else {
            throw new RuntimeException("Identity Document is required for KYC.");
        }

        // 2. Handle Merchant-specific data
        if (user.getRole() == User.Role.MERCHANT) {
            if (businessReg == null || businessReg.isEmpty()) {
                throw new RuntimeException("Business Registration (CAC) is required for Merchants.");
            }
            user.setBusinessRegistrationNumber(businessReg);
        }

        // 3. Handle Rider-specific data
        if (user.getRole() == User.Role.RIDER) {
            if (plateNumber == null || plateNumber.isEmpty()) {
                throw new RuntimeException("Vehicle Plate Number is required for Riders.");
            }
            user.setVehiclePlateNumber(plateNumber);
        }

        // Finalize state change
        user.setKycStatus(User.KycStatus.SUBMITTED);
        
        return userRepository.save(user);
    }
}