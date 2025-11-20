package com.homeheaven.controller;

import com.homeheaven.dto.request.PropertyRequest;
import com.homeheaven.dto.response.PropertyResponse;
import com.homeheaven.service.PropertyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/properties")
@RequiredArgsConstructor
@Slf4j
public class PropertyController {
    
    private final PropertyService propertyService;
    
    @PostMapping("/upload")
    public ResponseEntity<PropertyResponse> uploadProperty(
            @Valid @ModelAttribute PropertyRequest request,
            @RequestParam(value = "images", required = false) List<MultipartFile> images,
            Authentication authentication) {
        
        log.info("POST /api/properties/upload - user: {}", authentication.getName());
        PropertyResponse response = propertyService.createProperty(
                request, 
                authentication.getName(), 
                images
        );
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<PropertyResponse> getProperty(@PathVariable Long id) {
        log.info("GET /api/properties/{}", id);
        PropertyResponse response = propertyService.getPropertyById(id);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<PropertyResponse>> searchProperties(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) BigDecimal minRent,
            @RequestParam(required = false) BigDecimal maxRent,
            @RequestParam(required = false) String type) {
        
        log.info("GET /api/properties/search - city: {}, minRent: {}, maxRent: {}, type: {}", 
                city, minRent, maxRent, type);
        List<PropertyResponse> properties = propertyService.searchProperties(city, minRent, maxRent, type);
        return ResponseEntity.ok(properties);
    }
    
    @GetMapping("/my-properties")
    public ResponseEntity<List<PropertyResponse>> getMyProperties(Authentication authentication) {
        log.info("GET /api/properties/my-properties - user: {}", authentication.getName());
        List<PropertyResponse> properties = propertyService.getUserProperties(authentication.getName());
        return ResponseEntity.ok(properties);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<PropertyResponse> updateProperty(
            @PathVariable Long id,
            @Valid @ModelAttribute PropertyRequest request,
            Authentication authentication) {
        
        log.info("PUT /api/properties/{} - user: {}", id, authentication.getName());
        PropertyResponse response = propertyService.updateProperty(id, request, authentication.getName());
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProperty(@PathVariable Long id, Authentication authentication) {
        log.info("DELETE /api/properties/{} - user: {}", id, authentication.getName());
        propertyService.deleteProperty(id, authentication.getName());
        return ResponseEntity.ok("Property deleted successfully");
    }
}
