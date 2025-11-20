package com.homeheaven.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * OTP service for generating and validating OTPs (in-memory storage)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OtpService {
    
    private final EmailService emailService;
    
    @Value("${otp.expiration:600000}") // 10 minutes default
    private long otpExpiration;
    
    // In-memory storage: email -> OtpData
    private final Map<String, OtpData> otpStore = new ConcurrentHashMap<>();
    
    /**
     * Generate and send 4-digit OTP
     */
    public void generateAndSendOtp(String email) {
        // Generate random 4-digit OTP
        String otp = String.format("%04d", new Random().nextInt(10000));
        
        // Calculate expiration time
        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(otpExpiration / 1000);
        
        // Store OTP
        otpStore.put(email, new OtpData(otp, expiresAt));
        
        // Send email
        emailService.sendOtpEmail(email, otp);
        
        log.info("OTP generated and sent for email: {}", email);
    }
    
    /**
     * Verify OTP
     */
    public boolean verifyOtp(String email, String otp) {
        OtpData otpData = otpStore.get(email);
        
        if (otpData == null) {
            log.warn("No OTP found for email: {}", email);
            return false;
        }
        
        if (otpData.isExpired()) {
            log.warn("OTP expired for email: {}", email);
            otpStore.remove(email);
            return false;
        }
        
        if (!otpData.getCode().equals(otp)) {
            log.warn("Invalid OTP for email: {}", email);
            return false;
        }
        
        // OTP is valid, remove it
        otpStore.remove(email);
        log.info("OTP verified successfully for email: {}", email);
        return true;
    }
    
    /**
     * Clear OTP for email
     */
    public void clearOtp(String email) {
        otpStore.remove(email);
    }
    
    /**
     * Inner class to store OTP data
     */
    private static class OtpData {
        private final String code;
        private final LocalDateTime expiresAt;
        
        public OtpData(String code, LocalDateTime expiresAt) {
            this.code = code;
            this.expiresAt = expiresAt;
        }
        
        public String getCode() {
            return code;
        }
        
        public boolean isExpired() {
            return LocalDateTime.now().isAfter(expiresAt);
        }
    }
}
