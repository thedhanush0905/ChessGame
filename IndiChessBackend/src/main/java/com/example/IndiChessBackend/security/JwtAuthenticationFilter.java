package com.example.IndiChessBackend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

//import javax.servlet.Filter;
//import javax.servlet.FilterChain;
//import javax.servlet.FilterConfig;
//import javax.servlet.ServletException;
//import javax.servlet.annotation.WebFilter;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

@WebFilter(urlPatterns = "/*")  // This ensures that this filter is applied to all incoming requests
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final String secretKey = "your-secret-key";  // Replace with your actual secret key

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // Get the token from the Authorization header
        String token = getTokenFromRequest(request);

        if (token != null && validateToken(token)) {
            // If token is valid, set the authentication in the SecurityContext
            Claims claims = extractClaims(token);
            String username = claims.getSubject();  // Extract username from claims

            // Create authentication token
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>());
            SecurityContextHolder.getContext().setAuthentication(authentication);  // Set authentication in security context
        }

        filterChain.doFilter(request, response);  // Continue the request
    }

    // Helper method to extract token from the Authorization header
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);  // Extract the token by removing "Bearer " prefix
        }
        return null;
    }

    // Method to validate the JWT token
    private boolean validateToken(String token) {
        try {
            Claims claims = extractClaims(token);
            // Token validation (check if it's expired, signed correctly, etc.)
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return false;  // If token is invalid (e.g., expired, signature mismatch)
        }
    }

    // Method to extract claims from JWT token
    private Claims extractClaims(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
    }
}
