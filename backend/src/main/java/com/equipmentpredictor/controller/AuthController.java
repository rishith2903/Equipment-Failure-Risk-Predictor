package com.equipmentpredictor.controller;

import com.equipmentpredictor.dto.LoginRequest;
import com.equipmentpredictor.dto.LoginResponse;
import com.equipmentpredictor.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication Controller
 * Handles user login and JWT token generation
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = { "http://localhost:5173", "http://localhost:3000" })
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    @Value("${jwt.expiration}")
    private Long jwtExpirationMs;

    /**
     * Login endpoint
     * POST /api/v1/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()));

            String token = tokenProvider.generateToken(authentication);

            LoginResponse response = LoginResponse.builder()
                    .token(token)
                    .username(loginRequest.getUsername())
                    .expiresIn(jwtExpirationMs)
                    .build();

            log.info("User {} logged in successfully", loginRequest.getUsername());

            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            log.error("Login failed for user: {}", loginRequest.getUsername());
            return ResponseEntity.status(401).body("Invalid username or password");
        }
    }
}
