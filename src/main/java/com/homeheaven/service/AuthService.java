package com.homeheaven.service;

import com.homeheaven.dto.request.*;
import com.homeheaven.dto.response.AuthResponse;
import com.homeheaven.exception.BadRequestException;
import com.homeheaven.exception.UnauthorizedException;
import com.homeheaven.model.User;
import com.homeheaven.repository.UserRepository;
import com.homeheaven.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Authentication service for user registration, login, and password reset
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final OtpService otpService;
    
    /**
     * Register a new user
     */
    @Transactional
    public void register(RegisterRequest request) {
        log.info("Registering new user: {}", request.getUsername());
        
        // Validate passwords match
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("Passwords do not match");
        }
        
        // Check if username exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username already exists");
        }
        
        // Check if email exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists");
        }
        
        // Create user
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .phone(request.getPhone())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .pin(request.getPin())
                .role(User.Role.USER)
                .isActive(true)
                .build();
        
        userRepository.save(user);
        log.info("User registered successfully: {}", user.getUsername());
    }
    
    /**
     * Login user and generate JWT token
     */
    @Transactional
    public AuthResponse login(LoginRequest request) {
        log.info("User login attempt: {}", request.getUsername());
        
        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        
        // Generate token
        String token = tokenProvider.generateToken(authentication);
        
        // Update last login
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));
        
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);
        
        log.info("User logged in successfully: {}", user.getUsername());
        
        // Return response
        return AuthResponse.builder()
                .token(token)
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }
    
    /**
     * Send OTP to email for password reset
     */
    public void sendPasswordResetOtp(ForgotPasswordRequest request) {
        log.info("Sending OTP for password reset to email: {}", request.getEmail());
        
        // Check if user exists
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("No account found with this email"));
        
        // Generate and send OTP
        otpService.generateAndSendOtp(request.getEmail());
    }
    
    /**
     * Verify OTP and reset password
     */
    @Transactional
    public void verifyOtpAndResetPassword(VerifyOtpRequest request) {
        log.info("Verifying OTP and resetting password for email: {}", request.getEmail());
        
        // Validate passwords match
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("Passwords do not match");
        }
        
        // Verify OTP
        if (!otpService.verifyOtp(request.getEmail(), request.getOtp())) {
            throw new BadRequestException("Invalid or expired OTP");
        }
        
        // Find user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("User not found"));
        
        // Update password
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        
        log.info("Password reset successfully for user: {}", user.getUsername());
    }
    
    /**
     * Reset user password using email and PIN (legacy method - keep for backward compatibility)
     */
    @Transactional
    public void resetPassword(PasswordResetRequest request) {
        log.info("Password reset attempt for email: {}", request.getEmail());
        
        // Validate passwords match
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("Passwords do not match");
        }
        
        // Find user by email and PIN
        User user = userRepository.findByEmailAndPin(request.getEmail(), request.getPin())
                .orElseThrow(() -> new BadRequestException("Invalid email or PIN"));
        
        // Update password
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        
        log.info("Password reset successfully for user: {}", user.getUsername());
    }
}
