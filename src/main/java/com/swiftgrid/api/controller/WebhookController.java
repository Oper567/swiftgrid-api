package com.swiftgrid.api.controller;

import com.swiftgrid.api.model.Order;
import com.swiftgrid.api.model.User;
import com.swiftgrid.api.repository.OrderRepository;
import com.swiftgrid.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Optional;

@RestController
@RequestMapping("/api/webhooks")
public class WebhookController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    // Paystack will send a POST request here automatically
    @PostMapping("/paystack")
    public ResponseEntity<String> handlePaystackWebhook(@RequestBody String payload) {
        
        // In a real production app, we would verify Paystack's cryptographic signature here.
        // For our MVP, we will simulate a successful delivery webhook.
        
        // Let's pretend the payload tells us Order ID: a1b2c3d4... was delivered successfully.
        // We will do a manual override endpoint to test the math.
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

        User merchant = order.getCustomer(); // In a real flow, the order links directly to the product's merchant
        User rider = order.getRider();

        // THE MAGIC SPLIT (Assuming a ₦46,000 order)
        // 1. Rider gets ₦800 delivery fee
        rider.setWalletBalance(rider.getWalletBalance().add(new BigDecimal("800.00")));
        
        // 2. Merchant gets ₦45,000 for the shoes
        // (We are simplifying here; in reality we'd pull the merchant from the product table)
        // merchant.setWalletBalance(merchant.getWalletBalance().add(new BigDecimal("45000.00"))); 

        order.setStatus("DELIVERED");

        // Save everything to the database
        userRepository.save(rider);
        orderRepository.save(order);

        return ResponseEntity.ok("Success: Package Delivered! ₦800 added to Rider's Wallet.");
    }
}