package com.swiftgrid.api.repository;

import com.swiftgrid.api.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {
    // This will magically find all products belonging to a specific shop owner!
    List<Product> findByMerchantId(String merchantId);
}