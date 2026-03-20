package com.swiftgrid.api.repository;

import com.swiftgrid.api.model.Product; 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    // 1. Count items for the Dashboard stats (Perfect as is!)
    long countByMerchantId(String merchantId);
    
    // 🔥 UPGRADE: Automatically fetch and sort the catalog so the newest items are always at the top!
    List<Product> findByMerchantIdOrderByCreatedAtDesc(String merchantId);
}