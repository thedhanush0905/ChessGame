package com.example.IndiChessBackend.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenUtil {

    private final String secretKey = "your-secret-key";  // Secret key to sign the token
    private final long expirationTime = 86400000L;  // Token expiration time (24 hours)

    // Method to generate the JWT token
    public String generateToken(Authentication authentication) {
        String username = authentication.getName();  // Get the username from the authentication object

        return Jwts.builder()
                .setSubject(username)  // Set the subject as the username
                .setIssuedAt(new Date())  // Set the issue date
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))  // Set the expiration time
                .signWith(SignatureAlgorithm.HS256, secretKey)  // Sign the token with the secret key and HS256 algorithm
                .compact();
    }
}
