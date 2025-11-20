package com.homeheaven.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropertyResponse {
    private Long id;
    private String name;
    private String address;
    private String city;
    private String propertyType;
    private BigDecimal rent;
    private Integer sqft;
    private String sharingOption;
    private String description;
    private Boolean isAvailable;
    private Integer viewCount;
    private String images;
    private OwnerInfo owner;
    private LocalDateTime createdAt;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OwnerInfo {
        private String username;
        private String email;
        private String phone;
    }
}
