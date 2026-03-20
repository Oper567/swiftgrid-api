package com.swiftgrid.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    // 1. Catch specific Escrow/Business logic errors (e.g., Invalid OTP)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "success", false,
                "error", "Bad Request",
                "message", ex.getMessage(),
                "timestamp", LocalDateTime.now()
        ));
    }

    // 2. The Ultimate Safety Net: Catches literally everything else (Database down, Null Pointers, etc.)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleAllUncaughtException(Exception ex) {
        // In production, you would log 'ex.getMessage()' to a server file here, 
        // but hide the dirty details from the mobile user!
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "error", "Internal Server Error",
                "message", "An unexpected error occurred. Our engineers have been notified.",
                "timestamp", LocalDateTime.now()
        ));
    }
}