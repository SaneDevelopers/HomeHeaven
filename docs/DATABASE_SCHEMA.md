# HomeHeaven Database Schema Documentation

## Overview
This document describes the database schema for the HomeHeaven property rental platform.

## Database Information
- **Database Name**: homeheaven
- **Character Set**: utf8mb4
- **Collation**: utf8mb4_unicode_ci
- **Engine**: InnoDB

---

## Entity Relationship Diagram

```
┌─────────────┐         ┌──────────────┐         ┌──────────────────┐
│    users    │────────<│  properties  │────────<│ property_images  │
└─────────────┘    1:N  └──────────────┘    1:N  └──────────────────┘
      │                        │
      │ 1:N                    │ 1:N
      │                        │
      ▼                        ▼
┌─────────────┐         ┌──────────────┐
│  favorites  │         │property_views│
└─────────────┘         └──────────────┘
      │
      │ N:1
      ▼
┌──────────────┐
│  properties  │
└──────────────┘
```

---

## Tables

### 1. users
Stores user account information.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique user identifier |
| username | VARCHAR(50) | UNIQUE, NOT NULL | User's login name |
| email | VARCHAR(100) | UNIQUE, NOT NULL | User's email address |
| password_hash | VARCHAR(255) | NOT NULL | BCrypt hashed password |
| phone | VARCHAR(20) | NULL | User's phone number |
| pin | VARCHAR(4) | NOT NULL | 4-digit PIN for password recovery |
| role | ENUM('USER', 'ADMIN') | DEFAULT 'USER' | User role |
| is_active | BOOLEAN | DEFAULT TRUE | Account status |
| created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Account creation time |
| updated_at | TIMESTAMP | AUTO UPDATE | Last update time |
| last_login | TIMESTAMP | NULL | Last login timestamp |

**Indexes:**
- PRIMARY KEY (id)
- UNIQUE (username)
- UNIQUE (email)
- INDEX (username)
- INDEX (email)
- INDEX (created_at)

---

### 2. properties
Stores property listings.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique property identifier |
| owner_id | BIGINT | FOREIGN KEY, NOT NULL | Reference to users.id |
| name | VARCHAR(200) | NOT NULL | Property name/title |
| address | TEXT | NOT NULL | Full address |
| city | VARCHAR(100) | NOT NULL | City name |
| property_type | ENUM | NOT NULL | PG, Hostel, Flat, House |
| rent | DECIMAL(10,2) | NOT NULL | Monthly rent amount |
| sqft | INT | NULL | Area in square feet |
| sharing_option | VARCHAR(50) | NULL | Sharing type (for PG) |
| description | TEXT | NOT NULL | Detailed description |
| is_available | BOOLEAN | DEFAULT TRUE | Availability status |
| view_count | INT | DEFAULT 0 | Number of views |
| created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Creation time |
| updated_at | TIMESTAMP | AUTO UPDATE | Last update time |

**Indexes:**
- PRIMARY KEY (id)
- FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE CASCADE
- INDEX (owner_id)
- INDEX (city)
- INDEX (property_type)
- INDEX (rent)
- INDEX (created_at)
- INDEX (is_available)
- FULLTEXT INDEX (name, description, city)

---

### 3. property_images
Stores property image references.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique image identifier |
| property_id | BIGINT | FOREIGN KEY, NOT NULL | Reference to properties.id |
| image_path | VARCHAR(500) | NOT NULL | Image file path |
| is_primary | BOOLEAN | DEFAULT FALSE | Primary image flag |
| display_order | INT | DEFAULT 0 | Display order |
| uploaded_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Upload time |

**Indexes:**
- PRIMARY KEY (id)
- FOREIGN KEY (property_id) REFERENCES properties(id) ON DELETE CASCADE
- INDEX (property_id)
- INDEX (is_primary)

---

### 4. favorites
Stores user's favorite properties.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique favorite identifier |
| user_id | BIGINT | FOREIGN KEY, NOT NULL | Reference to users.id |
| property_id | BIGINT | FOREIGN KEY, NOT NULL | Reference to properties.id |
| created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Creation time |

**Indexes:**
- PRIMARY KEY (id)
- FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
- FOREIGN KEY (property_id) REFERENCES properties(id) ON DELETE CASCADE
- UNIQUE KEY (user_id, property_id)
- INDEX (user_id)
- INDEX (property_id)

---

### 5. property_views
Tracks property view statistics.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique view identifier |
| property_id | BIGINT | FOREIGN KEY, NOT NULL | Reference to properties.id |
| user_id | BIGINT | FOREIGN KEY, NULL | Reference to users.id |
| ip_address | VARCHAR(45) | NULL | Viewer's IP address |
| user_agent | TEXT | NULL | Browser user agent |
| viewed_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | View timestamp |

**Indexes:**
- PRIMARY KEY (id)
- FOREIGN KEY (property_id) REFERENCES properties(id) ON DELETE CASCADE
- FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
- INDEX (property_id)
- INDEX (viewed_at)

---

### 6. contact_requests
Stores contact requests between users (Future Enhancement).

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique request identifier |
| property_id | BIGINT | FOREIGN KEY, NOT NULL | Reference to properties.id |
| requester_id | BIGINT | FOREIGN KEY, NOT NULL | Reference to users.id |
| message | TEXT | NULL | Request message |
| status | ENUM | DEFAULT 'PENDING' | PENDING, CONTACTED, CLOSED |
| created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Creation time |
| updated_at | TIMESTAMP | AUTO UPDATE | Last update time |

**Indexes:**
- PRIMARY KEY (id)
- FOREIGN KEY (property_id) REFERENCES properties(id) ON DELETE CASCADE
- FOREIGN KEY (requester_id) REFERENCES users(id) ON DELETE CASCADE
- INDEX (property_id)
- INDEX (requester_id)
- INDEX (status)

---

## Sample Data

### Users (4 records)
- john_doe (USER)
- jane_smith (USER)
- admin_user (ADMIN)
- test_user (USER)

**Default Password**: password123 (for all sample users)

### Properties (6 records)
1. Cozy 2BHK Apartment in Andheri, Mumbai
2. Comfortable PG for Girls in Koramangala, Bangalore
3. Spacious 3BHK House in Bandra, Mumbai
4. Modern Hostel for Students, Pune
5. Affordable 1BHK Flat in Whitefield, Bangalore
6. Premium PG for Boys in HSR Layout, Bangalore

### Property Images (17 records)
- Multiple images per property
- Primary image marked for each property

---

## Database Statistics

```sql
-- Total users
SELECT COUNT(*) FROM users;
-- Result: 4

-- Total properties
SELECT COUNT(*) FROM properties;
-- Result: 6

-- Total images
SELECT COUNT(*) FROM property_images;
-- Result: 17

-- Properties by city
SELECT city, COUNT(*) as count FROM properties GROUP BY city;
-- Mumbai: 2, Bangalore: 3, Pune: 1

-- Properties by type
SELECT property_type, COUNT(*) as count FROM properties GROUP BY property_type;
-- Flat: 3, PG: 2, House: 1, Hostel: 1
```

---

## Migration Files

1. **V1__initial_schema.sql** - Creates all tables with indexes
2. **V2__seed_data.sql** - Inserts sample data for testing

---

## Setup Instructions

### 1. Create Database
```bash
mysql -u root -e "CREATE DATABASE homeheaven CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
```

### 2. Run Migrations
```bash
mysql -u root homeheaven < src/main/resources/db/migration/V1__initial_schema.sql
mysql -u root homeheaven < src/main/resources/db/migration/V2__seed_data.sql
```

### 3. Verify Setup
```bash
mysql -u root homeheaven -e "SHOW TABLES;"
```

---

## Notes

- All tables use InnoDB engine for transaction support
- Foreign keys have CASCADE delete for data integrity
- Indexes are optimized for common queries
- FULLTEXT index on properties for search functionality
- Timestamps are automatically managed by MySQL
- BCrypt is used for password hashing (cost factor: 10)

---

**Last Updated**: November 19, 2025
**Version**: 1.0
**Status**: ✅ Completed
