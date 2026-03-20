package com.swiftgrid.api.service;
import org.springframework.stereotype.Service;

@Service
public class PaystackService {
    public boolean sendMoney(String recipientCode, int amountKobo, String reason) {
        // MVP Mock: Always returns true assuming the bank transfer worked!
        System.out.println("Transferred " + amountKobo + " kobo to " + recipientCode + " for " + reason);
        return true; 
    }
}