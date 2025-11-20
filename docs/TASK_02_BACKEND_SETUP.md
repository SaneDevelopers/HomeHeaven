# Task 02: Backend Setup & Core Structure

## 🎯 Objective
Setup Spring Boot project structure with proper configuration and create core entity models.

---

## 📁 Project Structure

```
src/main/java/com/homeheaven/
├── HomeHeavenApplication.java          # Main application class
│
├── config/                             # Configuration classes
│   ├── SecurityConfig.java             # Spring Security configuration
│   ├── WebConfig.java                  # Web MVC configuration
│   ├── JwtConfig.java                  # JWT configuration
│   └── FileStorageConfig.java          # File upload configuration
│
├── controller/                         # REST Controllers
│   ├── AuthController.java             # Authentication endpoints
│   ├── PropertyController.java         # Property CRUD endpoints
│   ├── UserController.java             # User management
│   └── FileController.java             # File upload/download
│
├── service/                            # Business Logic
│   ├── AuthService.java
│   ├── PropertyService.java
│   ├── UserService.java
│   ├── FileStorageService.java
│   └── EmailService.java
│
├── repository/                         # Data Access
│   ├── UserRepository.java
│   ├── PropertyRepository.java
│   ├── PropertyImageRepository.java
│   └── FavoriteRepository.java
│
├── model/                              # JPA Entities
│   ├── User.java
│   ├── Property.java
│   ├── PropertyImage.java
│   ├── Favorite.java
│   └── PropertyView.java
│
├── dto/                                # Data Transfer Objects
│   ├── request/
│   │   ├── LoginRequest.java
│   │   ├── RegisterRequest.java
│   │   ├── PropertyRequest.java
│   │   └── PasswordResetRequest.java
│   └── response/
│       ├── AuthResponse.java
│       ├── PropertyResponse.java
│       ├── UserResponse.java
│       └── ApiResponse.java
│
├── security/                           # Security Components
│   ├── JwtTokenProvider.java          # JWT token generation/validation
│   ├── JwtAuthenticationFilter.java   # JWT filter
│   ├── CustomUserDetailsService.java  # User details service
│   └── SecurityUtils.java             # Security utilities
│
├── exception/                          # Exception Handling
│   ├── GlobalExceptionHandler.java    # Global exception handler
│   ├── ResourceNotFoundException.java
│   ├── UnauthorizedException.java
│   ├── BadRequestException.java
│   └── FileStorageException.java
│
└── util/                               # Utility Classes
    ├── ValidationUtils.java
    ├── FileUtils.java
    └── Constants.java
```

---

## 🔧 Configuration Files

### **application.properties**
```properties
# Application
spring.application.name=homeheaven
server.port=8080

# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/homeheaven?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=Pass@123
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

# File Upload Configuration
spring.servlet.multipart.enabled=true
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=50MB
file.upload-dir=./uploads

# JWT Configuration
jwt.secret=your-secret-key-change-this-in-production-min-256-bits
jwt.expiration=86400000

# Logging
logging.level.com.homeheaven=DEBUG
logging.level.org.springframework.security=DEBUG
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss} - %msg%n

# Error Handling
server.error.include-message=always
server.error.include-binding-errors=always
```

### **application-prod.properties** (Production)
```properties
# Production Database
spring.datasource.url=jdbc:mysql://prod-db-host:3306/homeheaven
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

# JPA
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false

# Logging
logging.level.com.homeheaven=INFO
logging.level.org.springframework.security=WARN

# JWT
jwt.secret=${JWT_SECRET}
jwt.expiration=86400000
```

---

## 📦 Maven Dependencies (pom.xml)

```xml
<dependencies>
    <!-- Spring Boot Starters -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
    
    <!-- Database -->
    <dependency>
        <groupId>com.mysql</groupId>
        <artifactId>mysql-connector-j</artifactId>
        <scope>runtime</scope>
    </dependency>
    
    <!-- JWT -->
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-api</artifactId>
        <version>0.11.5</version>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-impl</artifactId>
        <version>0.11.5</version>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-jackson</artifactId>
        <version>0.11.5</version>
        <scope>runtime</scope>
    </dependency>
    
    <!-- Lombok -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
    
    <!-- Apache Commons -->
    <dependency>
        <groupId>commons-io</groupId>
        <artifactId>commons-io</artifactId>
        <version>2.15.1</version>
    </dependency>
    
    <!-- MapStruct (DTO Mapping) -->
    <dependency>
        <groupId>org.mapstruct</groupId>
        <artifactId>mapstruct</artifactId>
        <version>1.5.5.Final</version>
    </dependency>
    
    <!-- DevTools -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-devtools</artifactId>
        <scope>runtime</scope>
        <optional>true</optional>
    </dependency>
    
    <!-- Testing -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
    
    <dependency>
        <groupId>org.springframework.security</groupId>
        <artifactId>spring-security-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

---

## 🏗️ Core Entity Models

### **User.java**
```java
package com.homeheaven.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false, length = 50)
    private String username;
    
    @Column(unique = true, nullable = false, length = 100)
    private String email;
    
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;
    
    @Column(length = 20)
    private String phone;
    
    @Column(nullable = false, length = 4)
    private String pin;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "last_login")
    private LocalDateTime lastLogin;
    
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Property> properties = new ArrayList<>();
    
    public enum Role {
        USER, ADMIN
    }
}
```

### **Property.java**
```java
package com.homeheaven.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "properties")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Property {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
    
    @Column(nullable = false, length = 200)
    private String name;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String address;
    
    @Column(nullable = false, length = 100)
    private String city;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "property_type", nullable = false)
    private PropertyType propertyType;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal rent;
    
    private Integer sqft;
    
    @Column(name = "sharing_option", length = 50)
    private String sharingOption;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "is_available")
    private Boolean isAvailable = true;
    
    @Column(name = "view_count")
    private Integer viewCount = 0;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PropertyImage> images = new ArrayList<>();
    
    public enum PropertyType {
        PG, Hostel, Flat, House
    }
}
```

### **PropertyImage.java**
```java
package com.homeheaven.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "property_images")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PropertyImage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;
    
    @Column(name = "image_path", nullable = false, length = 500)
    private String imagePath;
    
    @Column(name = "is_primary")
    private Boolean isPrimary = false;
    
    @Column(name = "display_order")
    private Integer displayOrder = 0;
    
    @CreationTimestamp
    @Column(name = "uploaded_at", updatable = false)
    private LocalDateTime uploadedAt;
}
```

---

## ✅ Implementation Checklist

### Setup
- [ ] Create Spring Boot project structure
- [ ] Add Maven dependencies
- [ ] Configure application.properties
- [ ] Setup database connection
- [ ] Test database connectivity

### Entity Models
- [ ] Create User entity
- [ ] Create Property entity
- [ ] Create PropertyImage entity
- [ ] Add validation annotations
- [ ] Test entity relationships

### Repositories
- [ ] Create UserRepository
- [ ] Create PropertyRepository
- [ ] Create PropertyImageRepository
- [ ] Add custom query methods
- [ ] Test repository methods

### Configuration
- [ ] Create WebConfig
- [ ] Create FileStorageConfig
- [ ] Setup CORS configuration
- [ ] Configure error handling
- [ ] Test configurations

---

## 🧪 Testing

```java
@SpringBootTest
class UserRepositoryTest {
    
    @Autowired
    private UserRepository userRepository;
    
    @Test
    void testCreateUser() {
        User user = User.builder()
            .username("testuser")
            .email("test@example.com")
            .passwordHash("hashedpassword")
            .pin("1234")
            .build();
            
        User saved = userRepository.save(user);
        assertNotNull(saved.getId());
    }
}
```

---

**Status**: ✅ COMPLETED
**Time Taken**: 1.5 hours
**Dependencies**: TASK_01 (Database Design) ✅
**Next Task**: TASK_03_AUTHENTICATION.md
