package com.swiftgrid.api.repository;

import com.swiftgrid.api.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
    // This magically finds all orders waiting for a rider!
    List<Order> findByStatus(String status);
}