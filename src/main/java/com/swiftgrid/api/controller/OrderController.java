package com.swiftgrid.api.controller;

import com.swiftgrid.api.model.Order;
import com.swiftgrid.api.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    // ... [KEEP YOUR EXISTING /checkout METHOD HERE] ...

    // 🔐 THE ESCROW RELEASE PROTOCOL
    @PostMapping("/verify-otp/{orderId}")
    public ResponseEntity<?> verifyDeliveryOtp(@PathVariable Long orderId, @RequestBody Map<String, String> payload) {
        try {
            String submittedOtp = payload.get("otp");

            Optional<Order> orderOpt = orderRepository.findById(orderId);
            if (orderOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Order not found."));
            }

            Order order = orderOpt.get();

            // 1. Check if the order is already released
            if ("RELEASED".equals(order.getStatus())) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Funds have already been released."));
            }

            // 2. The Truth Test: Does the OTP match?
            if (order.getOtpCode() != null && order.getOtpCode().equals(submittedOtp)) {
                
                // BOOM! The OTP is correct. Release the funds to the merchant.
                order.setStatus("RELEASED");
                orderRepository.save(order);
                
                return ResponseEntity.ok(Map.of("success", true, "message", "OTP Verified! Funds released to Joshua."));
            } else {
                // Incorrect OTP
                return ResponseEntity.status(401).body(Map.of("success", false, "message", "Invalid OTP. Funds remain locked."));
            }

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("success", false, "message", "Verification failed."));
        }
    }

    public record OrderRequest(
        String productId, 
        String merchantId, 
        String customerId, 
        BigDecimal amount
    ) {}
}