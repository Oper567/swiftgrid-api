package com.swiftgrid.api.service;

import com.swiftgrid.api.model.MerchantStats;
import com.swiftgrid.api.repository.ProductRepository;
import com.swiftgrid.api.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class DashboardService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    public MerchantStats getStats(String merchantId) {
        // 1. Calculate Escrow Balance (Orders where status is 'LOCKED')
        BigDecimal escrow = orderRepository.sumEscrowByMerchantId(merchantId);
        if (escrow == null) escrow = BigDecimal.ZERO;

        // 2. Calculate Cleared Earnings (Orders where status is 'RELEASED')
        BigDecimal cleared = orderRepository.sumClearedByMerchantId(merchantId);
        if (cleared == null) cleared = BigDecimal.ZERO;

        // 3. Count Items Listed
        long items = productRepository.countByMerchantId(merchantId);

        // 4. Count Active Deliveries
        long active = orderRepository.countActiveDeliveriesByMerchantId(merchantId);

        return new MerchantStats(
            escrow.doubleValue(),
            cleared.doubleValue(),
            (int) items,
            (int) active
        );
    }
}