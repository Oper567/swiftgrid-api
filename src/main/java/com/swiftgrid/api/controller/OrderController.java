package com.swiftgrid.api.controller;

import com.swiftgrid.api.model.Order;
import com.swiftgrid.api.model.User;
import com.swiftgrid.api.repository.OrderRepository;
import com.swiftgrid.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/checkout/{customerId}")
    public ResponseEntity<?> createOrder(@PathVariable String customerId, @RequestBody Order newOrder) {
        
        // Step 1: Verify the buyer exists
        Optional<User> customerOpt = userRepository.findById(customerId);

        if (customerOpt.isEmpty() || !customerOpt.get().getRole().equals("CUSTOMER")) {
            return ResponseEntity.badRequest().body("Error: Valid Customer account required to checkout.");
        }

        // Step 2: Attach the buyer to the order
        newOrder.setCustomer(customerOpt.get());
        
        // Step 3: Lock it in Escrow (Pending Rider)
        newOrder.setStatus("PENDING");

        // Save to the vault
        orderRepository.save(newOrder);

        return ResponseEntity.ok("Success: Order placed! Escrow locked for ₦" + newOrder.getTotalAmount() + ". Waiting for a SwiftGrid Rider.");
    }
    
    @PutMapping("/accept/{orderId}/rider/{riderId}")
    public ResponseEntity<?> acceptOrder(@PathVariable String orderId, @PathVariable String riderId) {
        
        // Step 1: Find the Order
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Error: Order not found!");
        }

        // Step 2: Find the Rider
        Optional<User> riderOpt = userRepository.findById(riderId);
        if (riderOpt.isEmpty() || !riderOpt.get().getRole().equals("RIDER")) {
            return ResponseEntity.badRequest().body("Error: Valid Rider account required!");
        }

        Order order = orderOpt.get();

        // Step 3: Prevent double-booking
        if (!order.getStatus().equals("PENDING")) {
            return ResponseEntity.badRequest().body("Too late! Another rider already claimed this order.");
        }

        // Step 4: Lock the Rider to the Order
        order.setRider(riderOpt.get());
        order.setStatus("EN_ROUTE");
        
        // Save the update to PostgreSQL
        orderRepository.save(order);

        return ResponseEntity.ok("Success: Rider dispatched! Package is EN_ROUTE.");
    }
}