package com.homeheaven.repository;

import com.homeheaven.model.Property;
import com.homeheaven.model.PropertyImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for PropertyImage entity
 */
@Repository
public interface PropertyImageRepository extends JpaRepository<PropertyImage, Long> {
    
    /**
     * Find all images for a property
     */
    List<PropertyImage> findByPropertyOrderByDisplayOrderAsc(Property property);
    
    /**
     * Find primary image for a property
     */
    Optional<PropertyImage> findByPropertyAndIsPrimaryTrue(Property property);
    
    /**
     * Delete all images for a property
     */
    void deleteByProperty(Property property);
    
    /**
     * Count images for a property
     */
    long countByProperty(Property property);
}
