package com.swiftgrid.api.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class PaystackService {

    @Value("${paystack.secret.key}")
    private String paystackSecretKey;

    private final RestTemplate restTemplate = new RestTemplate();

    // This method physically commands Paystack to move money to a bank account
    public boolean sendMoney(String recipientCode, int amountInKobo, String reason) {
        
        String url = "https://api.paystack.co/transfer";

        // 1. Set up the ID Card (Headers)
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(paystackSecretKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 2. Build the exact JSON package Paystack expects
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("source", "balance"); 
        requestBody.put("amount", amountInKobo); // Paystack calculates in Kobo! So ₦800 is 80000
        requestBody.put("recipient", recipientCode); // The Rider's or Merchant's bank account ID
        requestBody.put("reason", reason);

        // 3. Package it all together
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            // 4. FIRE THE COMMAND TO PAYSTACK!
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            
            // If Paystack says 200 OK, the money has moved.
            return response.getStatusCode() == HttpStatus.OK;
            
        } catch (Exception e) {
            System.out.println("Paystack Error: " + e.getMessage());
            return false;
        }
    }
}