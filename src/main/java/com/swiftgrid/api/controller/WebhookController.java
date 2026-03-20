package com.swiftgrid.api.controller;

import com.swiftgrid.api.model.Order;
import com.swiftgrid.api.repository.OrderRepository;
import com.swiftgrid.api.service.PaystackService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/webhooks")
public class WebhookController {

    private final OrderRepository orderRepository;
    private final PaystackService paystackService;

    public WebhookController(OrderRepository orderRepository, PaystackService paystackService) {
        this.orderRepository = orderRepository;
        this.paystackService = paystackService;
    }

    // FIX 1: Changed 'String orderId' to 'Long orderId'. Spring Boot converts it automatically!
    @PutMapping("/simulate-delivery/{orderId}")
    public ResponseEntity<?> simulateDelivery(@PathVariable Long orderId) {
        
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        
        if (orderOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Order not found."));
        }

        Order order = orderOpt.get();
        
        // FIX 2: Changed "EN_ROUTE" to "IN_TRANSIT" to match your Dashboard Analytics queries
        if (!"IN_TRANSIT".equals(order.getStatus())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Package must be IN_TRANSIT."));
        }

        // Simulating the Paystack Transfers
        boolean riderPaid = paystackService.sendMoney("RCP_1a2b3c", 80000, "Delivery Fee");
        boolean merchantPaid = paystackService.sendMoney("RCP_9z8y7x", 4500000, "Payout");

        if (riderPaid && merchantPaid) {
            // FIX 3: Changed to "RELEASED" so Joshua's "Cleared Earnings" goes up on his Flutter App!
            order.setStatus("RELEASED");
        } else {
            order.setStatus("DELIVERED_PAYOUT_FAILED"); 
        }

        orderRepository.save(order);
        return ResponseEntity.ok(Map.of("message", "Success!", "status", order.getStatus()));
    }
}