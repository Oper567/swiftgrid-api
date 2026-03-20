package com.swiftgrid.api.controller;

import com.swiftgrid.api.model.Product;
import com.swiftgrid.api.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    // 1. ADD NEW PRODUCT (Catches data from your Flutter AddProductScreen)
    @PostMapping("/add")
    public ResponseEntity<?> addProduct(@RequestBody Product product) {
        try {
            // Saves the name, price, and Supabase Image URL to your database!
            Product savedProduct = productRepository.save(product);
            return ResponseEntity.ok(savedProduct);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to add product: " + e.getMessage());
        }
    }

    // 2. FETCH CATALOG (Feeds your Flutter InventoryScreen)
    @GetMapping("/merchant/{merchantId}")
    public ResponseEntity<List<Product>> getMerchantProducts(@PathVariable String merchantId) {
        List<Product> products = productRepository.findByMerchantId(merchantId);
        return ResponseEntity.ok(products);
    }

    // 3. UPDATE PRODUCT (Bonus: So Joshua can edit a price later)
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody Product productDetails) {
        return productRepository.findById(id).map(existing -> {
            existing.setName(productDetails.getName());
            existing.setPrice(productDetails.getPrice());
            existing.setDescription(productDetails.getDescription());
            existing.setImageUrl(productDetails.getImageUrl());
            existing.setStatus(productDetails.getStatus());
            
            Product updated = productRepository.save(existing);
            return ResponseEntity.ok(updated);
        }).orElse(ResponseEntity.notFound().build());
    }
}