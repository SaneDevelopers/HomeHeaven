package com.homeheaven.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * Request to verify OTP and reset password
 */
@Data
public class VerifyOtpRequest {
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;
    
    @NotBlank(message = "OTP is required")
    @Pattern(regexp = "^[0-9]{4}$", message = "OTP must be 4 digits")
    private String otp;
    
    @NotBlank(message = "New password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String newPassword;
    
    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;
}
