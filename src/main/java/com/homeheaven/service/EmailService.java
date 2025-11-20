package com.homeheaven.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Email service for sending emails
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    
    private final JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    /**
     * Send OTP email
     */
    public void sendOtpEmail(String toEmail, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("HomeHeaven - Password Reset OTP");
            message.setText(buildOtpEmailBody(otp));
            
            mailSender.send(message);
            log.info("OTP email sent successfully to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send OTP email to: {}", toEmail, e);
            throw new RuntimeException("Failed to send OTP email. Please try again later.");
        }
    }
    
    /**
     * Build OTP email body
     */
    private String buildOtpEmailBody(String otp) {
        return String.format("""
                Hello,
                
                Your OTP for password reset is: %s
                
                This OTP is valid for 10 minutes.
                
                If you didn't request this, please ignore this email.
                
                Best regards,
                HomeHeaven Team
                """, otp);
    }
}
