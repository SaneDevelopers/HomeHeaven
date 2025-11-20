# Task 04: Property Management Implementation

## 🎯 Objective
Implement complete property CRUD operations, image upload, search, and filtering functionality.

---

## 📋 Features to Implement

1. ✅ Create Property (with images)
2. ✅ View Property Details
3. ✅ Update Property
4. ✅ Delete Property
5. ✅ Search Properties (city, rent range, type)
6. ✅ List User's Properties
7. ✅ Image Upload & Management

---

## 🔧 Property Service

**File**: `src/main/java/com/homeheaven/service/PropertyService.java`

```java
package com.homeheaven.service;

import com.homeheaven.dto.request.PropertyRequest;
import com.homeheaven.dto.response.PropertyResponse;
import com.homeheaven.exception.ResourceNotFoundException;
import com.homeheaven.exception.UnauthorizedException;
import com.homeheaven.model.Property;
import com.homeheaven.model.PropertyImage;
import com.homeheaven.model.User;
import com.homeheaven.repository.PropertyRepository;
import com.homeheaven.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PropertyService {
    
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;
    
    @Transactional
    public PropertyResponse createProperty(PropertyRequest request, String username, List<MultipartFile> images) {
        // Get user
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        // Create property
        Property property = Property.builder()
                .owner(user)
                .name(request.getName())
                .address(request.getAddress())
                .city(request.getCity())
                .propertyType(Property.PropertyType.valueOf(request.getPropertyType()))
                .rent(new BigDecimal(request.getRent()))
                .sqft(request.getSqft())
                .sharingOption(request.getSharingOption())
                .description(request.getDescription())
                .isAvailable(true)
                .build();
        
        // Save property first to get ID
        property = propertyRepository.save(property);
        
        // Upload and save images
        if (images != null && !images.isEmpty()) {
            for (int i = 0; i < images.size(); i++) {
                String filename = fileStorageService.storeFile(images.get(i));
                PropertyImage image = PropertyImage.builder()
                        .property(property)
                        .imagePath(filename)
                        .isPrimary(i == 0)
                        .displayOrder(i)
                        .build();
                property.getImages().add(image);
            }
            property = propertyRepository.save(property);
        }
        
        return mapToResponse(property);
    }
    
    @Transactional(readOnly = true)
    public PropertyResponse getPropertyById(Long id) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));
        
        // Increment view count
        property.setViewCount(property.getViewCount() + 1);
        propertyRepository.save(property);
        
        return mapToResponse(property);
    }
    
    @Transactional(readOnly = true)
    public List<PropertyResponse> searchProperties(String city, BigDecimal minRent, BigDecimal maxRent, String type) {
        List<Property> properties;
        
        if (city != null && !city.isEmpty()) {
            properties = propertyRepository.findByCityContainingIgnoreCase(city);
        } else {
            properties = propertyRepository.findAll();
        }
        
        // Filter by rent range
        if (minRent != null) {
            properties = properties.stream()
                    .filter(p -> p.getRent().compareTo(minRent) >= 0)
                    .collect(Collectors.toList());
        }
        if (maxRent != null) {
            properties = properties.stream()
                    .filter(p -> p.getRent().compareTo(maxRent) <= 0)
                    .collect(Collectors.toList());
        }
        
        // Filter by type
        if (type != null && !type.isEmpty()) {
            properties = properties.stream()
                    .filter(p -> p.getPropertyType().name().equalsIgnoreCase(type))
                    .collect(Collectors.toList());
        }
        
        return properties.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<PropertyResponse> getUserProperties(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        List<Property> properties = propertyRepository.findByOwner(user);
        return properties.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public void deleteProperty(Long id, String username) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));
        
        // Check ownership
        if (!property.getOwner().getUsername().equals(username)) {
            throw new UnauthorizedException("You don't have permission to delete this property");
        }
        
        // Delete images from filesystem
        property.getImages().forEach(img -> fileStorageService.deleteFile(img.getImagePath()));
        
        // Delete property
        propertyRepository.delete(property);
    }
    
    private PropertyResponse mapToResponse(Property property) {
        return PropertyResponse.builder()
                .id(property.getId())
                .name(property.getName())
                .address(property.getAddress())
                .city(property.getCity())
                .propertyType(property.getPropertyType().name())
                .rent(property.getRent())
                .sqft(property.getSqft())
                .sharingOption(property.getSharingOption())
                .description(property.getDescription())
                .isAvailable(property.getIsAvailable())
                .viewCount(property.getViewCount())
                .images(property.getImages().stream()
                        .map(PropertyImage::getImagePath)
                        .collect(Collectors.joining(",")))
                .owner(mapOwnerToResponse(property.getOwner()))
                .createdAt(property.getCreatedAt())
                .build();
    }
    
    private PropertyResponse.OwnerInfo mapOwnerToResponse(User user) {
        return PropertyResponse.OwnerInfo.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .build();
    }
}
```

---

## 📋 Property DTOs

**PropertyRequest.java**
```java
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
```

**PropertyResponse.java**
```java
package com.homeheaven.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
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
    @AllArgsConstructor
    public static class OwnerInfo {
        private String username;
        private String email;
        private String phone;
    }
}
```

---

## 🎯 Property Controller

**File**: `src/main/java/com/homeheaven/controller/PropertyController.java`

```java
package com.homeheaven.controller;

import com.homeheaven.dto.request.PropertyRequest;
import com.homeheaven.dto.response.PropertyResponse;
import com.homeheaven.service.PropertyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/properties")
@RequiredArgsConstructor
public class PropertyController {
    
    private final PropertyService propertyService;
    
    @PostMapping("/upload")
    public ResponseEntity<PropertyResponse> uploadProperty(
            @Valid @ModelAttribute PropertyRequest request,
            @RequestParam(value = "images", required = false) List<MultipartFile> images,
            Authentication authentication) {
        
        PropertyResponse response = propertyService.createProperty(
                request, 
                authentication.getName(), 
                images
        );
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<PropertyResponse> getProperty(@PathVariable Long id) {
        PropertyResponse response = propertyService.getPropertyById(id);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<PropertyResponse>> searchProperties(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) BigDecimal minRent,
            @RequestParam(required = false) BigDecimal maxRent,
            @RequestParam(required = false) String type) {
        
        List<PropertyResponse> properties = propertyService.searchProperties(city, minRent, maxRent, type);
        return ResponseEntity.ok(properties);
    }
    
    @GetMapping("/my-properties")
    public ResponseEntity<List<PropertyResponse>> getMyProperties(Authentication authentication) {
        List<PropertyResponse> properties = propertyService.getUserProperties(authentication.getName());
        return ResponseEntity.ok(properties);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProperty(@PathVariable Long id, Authentication authentication) {
        propertyService.deleteProperty(id, authentication.getName());
        return ResponseEntity.ok("Property deleted successfully");
    }
}
```

---

## 📁 File Storage Service

**File**: `src/main/java/com/homeheaven/service/FileStorageService.java`

```java
package com.homeheaven.service;

import com.homeheaven.exception.FileStorageException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {
    
    private final Path fileStorageLocation;
    
    public FileStorageService(@Value("${file.upload-dir}") String uploadDir) {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Could not create upload directory", ex);
        }
    }
    
    public String storeFile(MultipartFile file) {
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        
        try {
            // Check if file is empty
            if (file.isEmpty()) {
                throw new FileStorageException("Failed to store empty file " + originalFilename);
            }
            
            // Check if filename contains invalid characters
            if (originalFilename.contains("..")) {
                throw new FileStorageException("Filename contains invalid path sequence " + originalFilename);
            }
            
            // Generate unique filename
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String filename = UUID.randomUUID().toString() + extension;
            
            // Copy file to target location
            Path targetLocation = this.fileStorageLocation.resolve(filename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            
            return filename;
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + originalFilename, ex);
        }
    }
    
    public void deleteFile(String filename) {
        try {
            Path filePath = this.fileStorageLocation.resolve(filename).normalize();
            Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            throw new FileStorageException("Could not delete file " + filename, ex);
        }
    }
}
```

---

## 🗄️ Property Repository

**File**: `src/main/java/com/homeheaven/repository/PropertyRepository.java`

```java
package com.homeheaven.repository;

import com.homeheaven.model.Property;
import com.homeheaven.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {
    
    List<Property> findByOwner(User owner);
    
    List<Property> findByCityContainingIgnoreCase(String city);
    
    List<Property> findByPropertyType(Property.PropertyType propertyType);
    
    List<Property> findByRentBetween(BigDecimal minRent, BigDecimal maxRent);
    
    @Query("SELECT p FROM Property p WHERE p.isAvailable = true ORDER BY p.createdAt DESC")
    List<Property> findAllAvailableProperties();
    
    @Query("SELECT p FROM Property p WHERE " +
           "(:city IS NULL OR LOWER(p.city) LIKE LOWER(CONCAT('%', :city, '%'))) AND " +
           "(:minRent IS NULL OR p.rent >= :minRent) AND " +
           "(:maxRent IS NULL OR p.rent <= :maxRent) AND " +
           "(:type IS NULL OR p.propertyType = :type)")
    List<Property> searchProperties(String city, BigDecimal minRent, BigDecimal maxRent, Property.PropertyType type);
}
```

---

## ✅ Implementation Checklist

- [ ] Create Property Service
- [ ] Create File Storage Service
- [ ] Create Property DTOs
- [ ] Create Property Controller
- [ ] Create Property Repository
- [ ] Add validation
- [ ] Test property creation
- [ ] Test image upload
- [ ] Test search functionality
- [ ] Test property deletion
- [ ] Add error handling
- [ ] Test ownership validation

---

**Status**: ⏳ Pending
**Estimated Time**: 10 hours
**Dependencies**: TASK_03 (Authentication)
**Next Task**: TASK_05_INTEGRATION.md
