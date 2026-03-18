package com.swiftgrid.api.controller;

import com.swiftgrid.api.model.Order;
import com.swiftgrid.api.model.User;
import com.swiftgrid.api.repository.OrderRepository;
import com.swiftgrid.api.repository.UserRepository;
import com.swiftgrid.api.service.PaystackService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/webhooks")
public class WebhookController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PaystackService paystackService;

    // Paystack will send a POST request here automatically
    @PostMapping("/paystack")
    public ResponseEntity<String> handlePaystackWebhook(@RequestBody String payload) {
        
        // In a real production app, we would verify Paystack's cryptographic signature here.
        // For our MVP, we will simulate a successful delivery webhook.
        return ResponseEntity.ok("Webhook endpoint is live and listening!");
    }

    // THIS IS OUR MVP TEST ENDPOINT TO SIMULATE THE RIDER DELIVERING THE PACKAGE
    @PutMapping("/simulate-delivery/{orderId}")
    public ResponseEntity<String> simulateDelivery(@PathVariable String orderId) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        
        if (orderOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Error: Order not found");
        }

        Order order = orderOpt.get();

        if (!order.getStatus().equals("EN_ROUTE")) {
            return ResponseEntity.badRequest().body("Error: Package must be EN_ROUTE to be delivered.");
        }

        // THE REAL PAYSTACK SPLIT (Assuming a ₦46,000 order)
        
        // 1. Move ₦800 to the Rider's real bank account (80000 kobo)
        // Note: "RCP_1a2b3c" is a placeholder for the Rider's actual Paystack Recipient Code
        boolean riderPaid = paystackService.sendMoney("RCP_1a2b3c", 80000, "SwiftGrid Delivery Fee - " + orderId);
        
        // 2. Move ₦45,000 to the Merchant's real bank account (4500000 kobo)
        boolean merchantPaid = paystackService.sendMoney("RCP_9z8y7x", 4500000, "SwiftGrid Payout for Nike Air Force 1");

        // 3. Status Logic: Did Paystack accept the transfer?
        if (riderPaid && merchantPaid) {
            order.setStatus("DELIVERED_AND_PAID");
        } else {
            // If the bank transfer fails (which it will right now because our RCP codes are fake)
            // we still mark it delivered so the rider can go home, but flag the payout for manual review.
            order.setStatus("DELIVERED_PAYOUT_FAILED"); 
        }

        // Save the updated status to the PostgreSQL vault
        orderRepository.save(order);

        return ResponseEntity.ok("Success: Package Delivered! Final Status: " + order.getStatus());
    }
}