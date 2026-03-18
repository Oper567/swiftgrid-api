package com.swiftgrid.api.controller;

import com.swiftgrid.api.model.Product;
import com.swiftgrid.api.model.User;
import com.swiftgrid.api.repository.ProductRepository;
import com.swiftgrid.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    // Notice we put the Merchant's ID directly in the URL!
    @PostMapping("/add/{merchantId}")
    public ResponseEntity<?> addProduct(@PathVariable String merchantId, @RequestBody Product newProduct) {
        
        // Step 1: Find the merchant in the database
        Optional<User> merchantOpt = userRepository.findById(merchantId);

        if (merchantOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Error: Merchant not found!");
        }

        // Step 2: Link the product to the merchant
        User merchant = merchantOpt.get();
        newProduct.setMerchant(merchant);

        // Step 3: Save to the vault
        productRepository.save(newProduct);

        return ResponseEntity.ok("Success: " + newProduct.getName() + " has been added to the Smart Shelf!");
    }
}