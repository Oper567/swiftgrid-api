package com.swiftgrid.api.repository;

import com.swiftgrid.api.model.Order; 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // 1. Sum up all money currently "Locked" for this merchant
    @Query("SELECT SUM(o.amount) FROM Order o WHERE o.merchantId = :merchantId AND o.status = 'LOCKED'")
    BigDecimal sumEscrowByMerchantId(@Param("merchantId") String merchantId);

    // 2. Sum up all money already "Released" to the merchant
    @Query("SELECT SUM(o.amount) FROM Order o WHERE o.merchantId = :merchantId AND o.status = 'RELEASED'")
    BigDecimal sumClearedByMerchantId(@Param("merchantId") String merchantId);

    // 3. Count how many orders are currently moving
    @Query("SELECT COUNT(o) FROM Order o WHERE o.merchantId = :merchantId AND o.status = 'IN_TRANSIT'")
    long countActiveDeliveriesByMerchantId(@Param("merchantId") String merchantId);
}