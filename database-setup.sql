-- HomeHeaven Database Setup Script
-- Run this script to create and initialize the database

-- Create database
CREATE DATABASE IF NOT EXISTS homeheaven 
    CHARACTER SET utf8mb4 
    COLLATE utf8mb4_unicode_ci;

-- Use the database
USE homeheaven;

-- Show confirmation
SELECT 'Database homeheaven created successfully!' AS Status;

-- To run the migration scripts, execute:
-- SOURCE src/main/resources/db/migration/V1__initial_schema.sql;
-- SOURCE src/main/resources/db/migration/V2__seed_data.sql;
