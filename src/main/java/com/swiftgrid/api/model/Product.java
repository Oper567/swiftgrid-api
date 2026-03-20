package com.swiftgrid.api.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime; // 🔥 Don't forget this import!

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    // 🔥 UPGRADE 1: Added precision and scale to handle Naira perfectly without rounding errors
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private String merchantId;

    private String imageUrl; // The URL from your Supabase Storage

    private String status = "AVAILABLE"; // AVAILABLE, LOCKED_IN_ESCROW, SOLD

    // 🔥 UPGRADE 2: Added the missing createdAt field so Spring Boot doesn't crash!
    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Product() {}

    // --- GENERATED GETTERS AND SETTERS ---
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public String getMerchantId() { return merchantId; }
    public void setMerchantId(String merchantId) { this.merchantId = merchantId; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    // 🔥 Added Getter and Setter for the new createdAt field
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}