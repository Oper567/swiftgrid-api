package com.swiftgrid.api.controller;

import com.swiftgrid.api.model.Order;
import com.swiftgrid.api.model.Product;
import com.swiftgrid.api.repository.OrderRepository;
import com.swiftgrid.api.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/merchant")
public class MerchantController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/dashboard/{merchantId}")
    public ResponseEntity<?> getDashboardStats(@PathVariable String merchantId) {
        
        // 1. Get all products owned by this merchant
        List<Product> products = productRepository.findByMerchantIdOrderByCreatedAtDesc(merchantId);
        
        // 2. In a real app, we'd filter orders by these products. 
        // For our MVP, let's just show the total successful payouts.
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalProducts", products.size());
        stats.put("activeOrders", 5); // Mocked for now
        stats.put("pendingEscrow", new BigDecimal("125000.00")); // Money waiting for riders
        stats.put("totalEarnings", new BigDecimal("450000.00")); // Realized profit

        return ResponseEntity.ok(stats);
    }
}