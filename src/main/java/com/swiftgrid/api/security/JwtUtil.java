package com.swiftgrid.api.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    private final long EXPIRATION_TIME = 86400000; // 24 hours

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(String email, String merchantId) {
        return Jwts.builder()
                .subject(email) // 🔥 New 0.12.x syntax
                .claim("merchantId", merchantId)
                .issuedAt(new Date()) // 🔥 New 0.12.x syntax
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // 🔥 New 0.12.x syntax
                .signWith(getSigningKey()) // 🔥 Algorithm is now automatically securely inferred!
                .compact();
    }
}