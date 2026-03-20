package com.swiftgrid.api.controller;

import com.swiftgrid.api.model.User; 
import com.swiftgrid.api.service.AuthService; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*") // 🌍 Wildcard for development to stop CORS headaches
public class AuthController {

    @Autowired
    private AuthService authService;

    // ==========================================
    // 1. REGISTRATION (String to Enum Mapping)
    // ==========================================
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest regRequest) {
        try {
            System.out.println("📩 Registration attempt: " + regRequest.getEmail());
            
            User user = new User();
            user.setEmail(regRequest.getEmail().trim());
            user.setPassword(regRequest.getPassword());
            
            // 🔥 CRITICAL: Convert Flutter String to Java Enum
            try {
                String roleInput = regRequest.getRole().toUpperCase();
                user.setRole(User.Role.valueOf(roleInput));
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(Map.of("message", "Invalid role. Use MERCHANT, CUSTOMER, or RIDER."));
            }

            // KycStatus is handled by AuthService.register() now!
            User savedUser = authService.register(user);
            return ResponseEntity.ok(savedUser); 
            
        } catch (Exception e) {
            System.err.println("❌ Registration Error: " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage())); 
        }
    }

    // ==========================================
    // 2. LOGIN
    // ==========================================
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
        try {
            System.out.println("🔐 Login attempt: " + loginRequest.getEmail());
            User user = authService.login(loginRequest.getEmail().trim(), loginRequest.getPassword());
            return ResponseEntity.ok(user); 
        } catch (Exception e) {
            System.err.println("❌ Login Error: " + e.getMessage());
            return ResponseEntity.status(401).body(Map.of("message", e.getMessage())); 
        }
    }

    // ==========================================
    // 3. KYC SUBMISSION
    // ==========================================
    @PostMapping("/{userId}/kyc")
    public ResponseEntity<?> submitKyc(@PathVariable Long userId, @RequestBody KycRequest kycRequest) {
        try {
            User updatedUser = authService.submitKycDocuments(
                userId, 
                kycRequest.getIdDocumentUrl(), 
                kycRequest.getBusinessRegistrationNumber(), 
                kycRequest.getVehiclePlateNumber()
            );
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "KYC submitted. Pending approval.",
                "user", updatedUser
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // --- DATA TRANSFER OBJECTS (DTOs) ---
    // These ensure the JSON from Flutter matches our Java expectations perfectly.
    
    public static class RegisterRequest {
        private String email;
        private String password;
        private String role;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }

    public static class LoginRequest {
        private String email;
        private String password;
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class KycRequest {
        private String idDocumentUrl;
        private String businessRegistrationNumber;
        private String vehiclePlateNumber;
        
        public String getIdDocumentUrl() { return idDocumentUrl; }
        public void setIdDocumentUrl(String url) { this.idDocumentUrl = url; }
        public String getBusinessRegistrationNumber() { return businessRegistrationNumber; }
        public void setBusinessRegistrationNumber(String b) { this.businessRegistrationNumber = b; }
        public String getVehiclePlateNumber() { return vehiclePlateNumber; }
        public void setVehiclePlateNumber(String v) { this.vehiclePlateNumber = v; }
    }
}