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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PropertyService {
    
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;
    
    @Transactional
    public PropertyResponse createProperty(PropertyRequest request, String username, List<MultipartFile> images) {
        log.info("Creating property for user: {}", username);
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
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
                .viewCount(0)
                .build();
        
        property = propertyRepository.save(property);
        log.info("Property created with ID: {}", property.getId());
        
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
            log.info("Uploaded {} images for property ID: {}", images.size(), property.getId());
        }
        
        return mapToResponse(property);
    }
    
    @Transactional
    public PropertyResponse getPropertyById(Long id) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));
        
        property.setViewCount(property.getViewCount() + 1);
        propertyRepository.save(property);
        
        return mapToResponse(property);
    }
    
    @Transactional(readOnly = true)
    public List<PropertyResponse> searchProperties(String city, BigDecimal minRent, BigDecimal maxRent, String type) {
        log.info("Searching properties - city: {}, minRent: {}, maxRent: {}, type: {}", city, minRent, maxRent, type);
        
        List<Property> properties;
        
        if (city != null && !city.isEmpty()) {
            properties = propertyRepository.findByCityContainingIgnoreCase(city);
        } else {
            properties = propertyRepository.findAll();
        }
        
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
        
        if (type != null && !type.isEmpty()) {
            properties = properties.stream()
                    .filter(p -> p.getPropertyType().name().equalsIgnoreCase(type))
                    .collect(Collectors.toList());
        }
        
        log.info("Found {} properties", properties.size());
        return properties.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<PropertyResponse> getUserProperties(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        List<Property> properties = propertyRepository.findByOwner(user);
        log.info("User {} has {} properties", username, properties.size());
        
        return properties.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public PropertyResponse updateProperty(Long id, PropertyRequest request, String username) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));
        
        if (!property.getOwner().getUsername().equals(username)) {
            throw new UnauthorizedException("You don't have permission to update this property");
        }
        
        property.setName(request.getName());
        property.setAddress(request.getAddress());
        property.setCity(request.getCity());
        property.setPropertyType(Property.PropertyType.valueOf(request.getPropertyType()));
        property.setRent(new BigDecimal(request.getRent()));
        property.setSqft(request.getSqft());
        property.setSharingOption(request.getSharingOption());
        property.setDescription(request.getDescription());
        
        property = propertyRepository.save(property);
        log.info("Property {} updated by user {}", id, username);
        
        return mapToResponse(property);
    }
    
    @Transactional
    public void deleteProperty(Long id, String username) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));
        
        if (!property.getOwner().getUsername().equals(username)) {
            throw new UnauthorizedException("You don't have permission to delete this property");
        }
        
        property.getImages().forEach(img -> fileStorageService.deleteFile(img.getImagePath()));
        
        propertyRepository.delete(property);
        log.info("Property {} deleted by user {}", id, username);
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
