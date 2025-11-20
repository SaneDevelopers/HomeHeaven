package com.homeheaven.repository;

import com.homeheaven.model.Property;
import com.homeheaven.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Repository for Property entity
 */
@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {
    
    /**
     * Find properties by owner
     */
    List<Property> findByOwner(User owner);
    
    /**
     * Find properties by city (case-insensitive)
     */
    List<Property> findByCityContainingIgnoreCase(String city);
    
    /**
     * Find properties by type
     */
    List<Property> findByPropertyType(Property.PropertyType propertyType);
    
    /**
     * Find properties by rent range
     */
    List<Property> findByRentBetween(BigDecimal minRent, BigDecimal maxRent);
    
    /**
     * Find all available properties
     */
    @Query("SELECT p FROM Property p WHERE p.isAvailable = true ORDER BY p.createdAt DESC")
    List<Property> findAllAvailableProperties();
    
    /**
     * Search properties with filters
     */
    @Query("SELECT p FROM Property p WHERE " +
           "(:city IS NULL OR LOWER(p.city) LIKE LOWER(CONCAT('%', :city, '%'))) AND " +
           "(:minRent IS NULL OR p.rent >= :minRent) AND " +
           "(:maxRent IS NULL OR p.rent <= :maxRent) AND " +
           "(:type IS NULL OR p.propertyType = :type) AND " +
           "p.isAvailable = true " +
           "ORDER BY p.createdAt DESC")
    List<Property> searchProperties(
            @Param("city") String city,
            @Param("minRent") BigDecimal minRent,
            @Param("maxRent") BigDecimal maxRent,
            @Param("type") Property.PropertyType type
    );
    
    /**
     * Count properties by owner
     */
    long countByOwner(User owner);
    
    /**
     * Count properties by availability
     */
    long countByIsAvailable(boolean isAvailable);
}
