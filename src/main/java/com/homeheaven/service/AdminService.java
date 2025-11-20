package com.homeheaven.service;

import com.homeheaven.dto.response.PropertyResponse;
import com.homeheaven.dto.response.UserResponse;
import com.homeheaven.exception.ResourceNotFoundException;
import com.homeheaven.model.Property;
import com.homeheaven.model.PropertyImage;
import com.homeheaven.model.User;
import com.homeheaven.repository.PropertyRepository;
import com.homeheaven.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {
    
    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;
    private final FileStorageService fileStorageService;
    
    @Transactional(readOnly = true)
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        
        long totalUsers = userRepository.count();
        long activeUsers = userRepository.countByIsActive(true);
        long totalProperties = propertyRepository.count();
        long availableProperties = propertyRepository.countByIsAvailable(true);
        
        stats.put("totalUsers", totalUsers);
        stats.put("activeUsers", activeUsers);
        stats.put("blockedUsers", totalUsers - activeUsers);
        stats.put("totalProperties", totalProperties);
        stats.put("availableProperties", availableProperties);
        stats.put("unavailableProperties", totalProperties - availableProperties);
        
        log.info("Dashboard stats retrieved: {} users, {} properties", totalUsers, totalProperties);
        return stats;
    }
    
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        log.info("Retrieved {} users", users.size());
        
        return users.stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public void blockUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        user.setIsActive(false);
        userRepository.save(user);
        log.info("User {} blocked", userId);
    }
    
    @Transactional
    public void unblockUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        user.setIsActive(true);
        userRepository.save(user);
        log.info("User {} unblocked", userId);
    }
    
    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        // Delete all user's properties and their images
        List<Property> properties = propertyRepository.findByOwner(user);
        properties.forEach(property -> {
            property.getImages().forEach(img -> fileStorageService.deleteFile(img.getImagePath()));
            propertyRepository.delete(property);
        });
        
        userRepository.delete(user);
        log.info("User {} and their {} properties deleted", userId, properties.size());
    }
    
    @Transactional(readOnly = true)
    public List<PropertyResponse> getAllProperties() {
        List<Property> properties = propertyRepository.findAll();
        log.info("Retrieved {} properties", properties.size());
        
        return properties.stream()
                .map(this::mapToPropertyResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public void deleteProperty(Long propertyId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));
        
        property.getImages().forEach(img -> fileStorageService.deleteFile(img.getImagePath()));
        propertyRepository.delete(property);
        log.info("Property {} deleted by admin", propertyId);
    }
    
    @Transactional
    public void togglePropertyAvailability(Long propertyId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));
        
        property.setIsAvailable(!property.getIsAvailable());
        propertyRepository.save(property);
        log.info("Property {} availability toggled to {}", propertyId, property.getIsAvailable());
    }
    
    private UserResponse mapToUserResponse(User user) {
        int propertyCount = propertyRepository.findByOwner(user).size();
        
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole().name())
                .isActive(user.getIsActive())
                .propertyCount(propertyCount)
                .createdAt(user.getCreatedAt())
                .lastLogin(user.getLastLogin())
                .build();
    }
    
    private PropertyResponse mapToPropertyResponse(Property property) {
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
                .owner(PropertyResponse.OwnerInfo.builder()
                        .username(property.getOwner().getUsername())
                        .email(property.getOwner().getEmail())
                        .phone(property.getOwner().getPhone())
                        .build())
                .createdAt(property.getCreatedAt())
                .build();
    }
}
