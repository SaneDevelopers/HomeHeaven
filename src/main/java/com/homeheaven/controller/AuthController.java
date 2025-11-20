package com.homeheaven.controller;

import com.homeheaven.dto.request.ForgotPasswordRequest;
import com.homeheaven.dto.request.LoginRequest;
import com.homeheaven.dto.request.PasswordResetRequest;
import com.homeheaven.dto.request.RegisterRequest;
import com.homeheaven.dto.request.VerifyOtpRequest;
import com.homeheaven.dto.response.AuthResponse;
import com.homeheaven.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication controller for user registration, login, and password reset
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    
    private final AuthService authService;
    
    /**
     * Register a new user
     */
    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request) {
        log.info("POST /api/auth/register - username: {}", request.getUsername());
        authService.register(request);
        return ResponseEntity.ok("User registered successfully");
    }
    
    /**
     * Login user
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("POST /api/auth/login - username: {}", request.getUsername());
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Send OTP for password reset
     */
    @PostMapping("/forgot-password/send-otp")
    public ResponseEntity<String> sendOtp(@Valid @RequestBody ForgotPasswordRequest request) {
        log.info("POST /api/auth/forgot-password/send-otp - email: {}", request.getEmail());
        authService.sendPasswordResetOtp(request);
        return ResponseEntity.ok("OTP sent to your email. Please check your inbox.");
    }
    
    /**
     * Verify OTP and reset password
     */
    @PostMapping("/forgot-password/verify-otp")
    public ResponseEntity<String> verifyOtpAndResetPassword(@Valid @RequestBody VerifyOtpRequest request) {
        log.info("POST /api/auth/forgot-password/verify-otp - email: {}", request.getEmail());
        authService.verifyOtpAndResetPassword(request);
        return ResponseEntity.ok("Password reset successfully");
    }
    
    /**
     * Reset password (legacy PIN-based method)
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody PasswordResetRequest request) {
        log.info("POST /api/auth/forgot-password - email: {}", request.getEmail());
        authService.resetPassword(request);
        return ResponseEntity.ok("Password reset successfully");
    }
    
    /**
     * Logout (client-side only for JWT)
     */
    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        log.info("POST /api/auth/logout");
        return ResponseEntity.ok("Logged out successfully");
    }
}
