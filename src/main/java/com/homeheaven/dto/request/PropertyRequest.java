package com.homeheaven.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class PropertyRequest {
    @NotBlank(message = "Property name is required")
    @Size(min = 5, max = 200, message = "Name must be between 5 and 200 characters")
    private String name;
    
    @NotBlank(message = "Address is required")
    private String address;
    
    @NotBlank(message = "City is required")
    private String city;
    
    @NotBlank(message = "Property type is required")
    private String propertyType;
    
    @NotNull(message = "Rent is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Rent must be positive")
    private String rent;
    
    private Integer sqft;
    
    private String sharingOption;
    
    @NotBlank(message = "Description is required")
    @Size(min = 100, message = "Description must be at least 100 characters")
    private String description;
}
