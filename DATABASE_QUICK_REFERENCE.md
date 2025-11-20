# Database Quick Reference

## Connection Details
```bash
Database: homeheaven
Host: localhost
Port: 3306
User: root
Password: (no password for local dev)
```

## Quick Commands

### Connect to Database
```bash
mysql -u root homeheaven
```

### View All Tables
```sql
SHOW TABLES;
```

### View Table Structure
```sql
DESCRIBE users;
DESCRIBE properties;
DESCRIBE property_images;
```

### Common Queries

#### Get All Users
```sql
SELECT id, username, email, role FROM users;
```

#### Get All Properties
```sql
SELECT id, name, city, property_type, rent FROM properties;
```

#### Get Property with Owner
```sql
SELECT p.*, u.username, u.email 
FROM properties p 
JOIN users u ON p.owner_id = u.id;
```

#### Get Property with Images
```sql
SELECT p.name, GROUP_CONCAT(pi.image_path) as images
FROM properties p
LEFT JOIN property_images pi ON p.id = pi.property_id
GROUP BY p.id;
```

#### Search Properties by City
```sql
SELECT * FROM properties WHERE city LIKE '%Mumbai%';
```

#### Search Properties by Rent Range
```sql
SELECT * FROM properties WHERE rent BETWEEN 10000 AND 30000;
```

## Sample Login Credentials

All users have password: `password123`

| Username | Email | Role | PIN |
|----------|-------|------|-----|
| john_doe | john@example.com | USER | 1234 |
| jane_smith | jane@example.com | USER | 5678 |
| admin_user | admin@homeheaven.com | ADMIN | 0000 |
| test_user | test@example.com | USER | 9999 |

## Reset Database

```bash
# Drop and recreate
mysql -u root -e "DROP DATABASE IF EXISTS homeheaven;"
mysql -u root -e "CREATE DATABASE homeheaven CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

# Run migrations
mysql -u root homeheaven < src/main/resources/db/migration/V1__initial_schema.sql
mysql -u root homeheaven < src/main/resources/db/migration/V2__seed_data.sql
```

## Backup Database

```bash
mysqldump -u root homeheaven > backup_$(date +%Y%m%d).sql
```

## Restore Database

```bash
mysql -u root homeheaven < backup_20251119.sql
```

---
**Last Updated**: November 19, 2025
