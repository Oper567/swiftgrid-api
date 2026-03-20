package com.swiftgrid.api.model;

/**
 * High-Fidelity Data Carrier for Merchant Analytics
 * This matches the exact JSON structure your Flutter app is expecting.
 */
public record MerchantStats(
    double escrowBalance,    // Total funds currently locked in escrow
    double clearedEarnings, // Total funds already paid out to the merchant
    int itemsListed,        // Count of all products in the merchant's catalog
    int activeDeliveries    // Count of orders currently 'In Transit'
) {}