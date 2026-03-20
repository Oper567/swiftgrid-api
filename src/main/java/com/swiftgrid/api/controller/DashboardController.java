package com.swiftgrid.api.controller;

import com.swiftgrid.api.model.MerchantStats;
import com.swiftgrid.api.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map; // <-- CRITICAL: Added this import to fix the compilation error!

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    private final DashboardService dashboardService;

    // PRO-TIP: Constructor Injection is much safer and faster than @Autowired
    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    // 📊 FETCH MERCHANT DASHBOARD STATS
    @GetMapping("/stats/{merchantId}")
    public ResponseEntity<MerchantStats> getMerchantStats(@PathVariable String merchantId) {
        MerchantStats stats = dashboardService.getStats(merchantId);
        return ResponseEntity.ok(stats);
    }

    // 💸 THE WITHDRAWAL PROTOCOL (MOCK PAYSTACK INTEGRATION)
    @PostMapping("/withdraw/{merchantId}")
    public ResponseEntity<Map<String, Object>> processWithdrawal(
            @PathVariable String merchantId, 
            @RequestBody Map<String, Object> payload) {
        
        try {
            // 1. Validation: Ensure they actually sent an amount
            if (!payload.containsKey("amount")) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false, 
                    "message", "Amount is required for withdrawal."
                ));
            }

            // 2. Parse the amount safely
            double amount = Double.parseDouble(payload.get("amount").toString());
            
            // 3. Simulating a 2-second bank processing delay
            Thread.sleep(2000); 

            // 4. Success Response
            return ResponseEntity.ok(Map.of(
                "success", true, 
                "message", "Successfully transferred ₦" + amount + " to your linked bank account."
            ));

        } catch (NumberFormatException e) {
            // Catches if a user tries to send "abc" instead of "5000"
            return ResponseEntity.badRequest().body(Map.of(
                "success", false, 
                "message", "Invalid amount format. Please enter a valid number."
            ));
        } catch (InterruptedException e) {
            // Required whenever you use Thread.sleep()
            Thread.currentThread().interrupt(); 
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false, 
                "message", "Bank transfer timed out. Try again."
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false, 
                "message", "Transfer failed: " + e.getMessage()
            ));
        }
    }
}