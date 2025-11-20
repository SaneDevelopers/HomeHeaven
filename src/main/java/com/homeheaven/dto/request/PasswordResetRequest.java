package com.homeheaven.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * Password reset request DTO
 */
@Data
public class PasswordResetRequest {
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;
    
    @NotBlank(message = "PIN is required")
    @Pattern(regexp = "^[0-9]{4}$", message = "PIN must be 4 digits")
    private String pin;
    
    @NotBlank(message = "New password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String newPassword;
    
    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;
}
