# Task 01: Database Design

## 🎯 Objective
Design a normalized, efficient database schema for HomeHeaven property rental platform.

---

## 📊 Database Schema

### **Table: users**
```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    pin VARCHAR(4) NOT NULL,
    role ENUM('USER', 'ADMIN') DEFAULT 'USER',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    last_login TIMESTAMP NULL,
    
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_created_at (created_at)
);
```

### **Table: properties**
```sql
CREATE TABLE properties (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    owner_id BIGINT NOT NULL,
    name VARCHAR(200) NOT NULL,
    address TEXT NOT NULL,
    city VARCHAR(100) NOT NULL,
    property_type ENUM('PG', 'Hostel', 'Flat', 'House') NOT NULL,
    rent DECIMAL(10, 2) NOT NULL,
    sqft INT NULL,
    sharing_option VARCHAR(50) NULL,
    description TEXT NOT NULL,
    is_available BOOLEAN DEFAULT TRUE,
    view_count INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_owner_id (owner_id),
    INDEX idx_city (city),
    INDEX idx_property_type (property_type),
    INDEX idx_rent (rent),
    INDEX idx_created_at (created_at),
    INDEX idx_available (is_available),
    FULLTEXT INDEX idx_search (name, description, city)
);
```

### **Table: property_images**
```sql
CREATE TABLE property_images (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    property_id BIGINT NOT NULL,
    image_path VARCHAR(500) NOT NULL,
    is_primary BOOLEAN DEFAULT FALSE,
    display_order INT DEFAULT 0,
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (property_id) REFERENCES properties(id) ON DELETE CASCADE,
    INDEX idx_property_id (property_id),
    INDEX idx_primary (is_primary)
);
```

### **Table: favorites** (Future Enhancement)
```sql
CREATE TABLE favorites (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    property_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (property_id) REFERENCES properties(id) ON DELETE CASCADE,
    UNIQUE KEY unique_favorite (user_id, property_id),
    INDEX idx_user_id (user_id),
    INDEX idx_property_id (property_id)
);
```

### **Table: property_views** (Analytics)
```sql
CREATE TABLE property_views (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    property_id BIGINT NOT NULL,
    user_id BIGINT NULL,
    ip_address VARCHAR(45),
    user_agent TEXT,
    viewed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (property_id) REFERENCES properties(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_property_id (property_id),
    INDEX idx_viewed_at (viewed_at)
);
```

### **Table: contact_requests** (Future Enhancement)
```sql
CREATE TABLE contact_requests (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    property_id BIGINT NOT NULL,
    requester_id BIGINT NOT NULL,
    message TEXT,
    status ENUM('PENDING', 'CONTACTED', 'CLOSED') DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (property_id) REFERENCES properties(id) ON DELETE CASCADE,
    FOREIGN KEY (requester_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_property_id (property_id),
    INDEX idx_requester_id (requester_id),
    INDEX idx_status (status)
);
```

---

## 🔗 Entity Relationships

```
users (1) ----< (N) properties
properties (1) ----< (N) property_images
users (N) ----< (N) properties (favorites)
properties (1) ----< (N) property_views
properties (1) ----< (N) contact_requests
users (1) ----< (N) contact_requests
```

---

## 📝 Data Constraints

### Users
- Username: 3-50 characters, alphanumeric + underscore
- Email: Valid email format
- Password: Min 6 characters (hashed with BCrypt)
- PIN: Exactly 4 digits (0000-9999)
- Phone: Optional, 10-15 digits

### Properties
- Name: 5-200 characters
- Rent: Positive number, max 10 digits
- Description: Min 100 words
- City: Required, 2-100 characters
- Images: Min 1, max 10 images per property

---

## 🎯 Indexing Strategy

### Primary Indexes
- All primary keys (id columns)
- Unique constraints (username, email)

### Search Optimization
- Fulltext index on (name, description, city)
- Index on city for location-based search
- Index on rent for price range queries
- Index on property_type for filtering

### Performance Indexes
- owner_id for user's properties
- created_at for sorting by date
- is_available for active listings

---

## 📊 Sample Data

### Users
```sql
INSERT INTO users (username, email, password_hash, phone, pin, role) VALUES
('john_doe', 'john@example.com', '$2a$10$...', '9876543210', '1234', 'USER'),
('jane_smith', 'jane@example.com', '$2a$10$...', '9876543211', '5678', 'USER'),
('admin', 'admin@homeheaven.com', '$2a$10$...', '9999999999', '0000', 'ADMIN');
```

### Properties
```sql
INSERT INTO properties (owner_id, name, address, city, property_type, rent, sqft, description) VALUES
(1, 'Cozy 2BHK Apartment', '123 Main St, Andheri', 'Mumbai', 'Flat', 25000.00, 850, 'Beautiful 2BHK apartment with modern amenities...'),
(2, 'Comfortable PG for Girls', '456 Park Road, Koramangala', 'Bangalore', 'PG', 8000.00, NULL, 'Safe and comfortable PG accommodation...');
```

---

## ✅ Checklist

- [ ] Review schema design
- [ ] Create database migration script
- [ ] Add sample data script
- [ ] Test foreign key constraints
- [ ] Verify indexes are created
- [ ] Test queries performance
- [ ] Document schema changes
- [ ] Create ER diagram

---

## 🔄 Migration Script

Create file: `src/main/resources/db/migration/V1__initial_schema.sql`

```sql
-- Drop tables if exist (for clean setup)
DROP TABLE IF EXISTS contact_requests;
DROP TABLE IF EXISTS property_views;
DROP TABLE IF EXISTS favorites;
DROP TABLE IF EXISTS property_images;
DROP TABLE IF EXISTS properties;
DROP TABLE IF EXISTS users;

-- Create tables (copy from above)
-- ...

-- Insert sample data
-- ...
```

---

## 📈 Next Steps

1. Review and approve schema
2. Create migration scripts
3. Setup database connection in Spring Boot
4. Create JPA entities (TASK_02)
5. Test database connectivity

---

**Status**: ⏳ Pending Review
**Estimated Time**: 4 hours
**Dependencies**: None
**Next Task**: TASK_02_BACKEND_SETUP.md
