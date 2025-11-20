-- HomeHeaven Sample Data
-- Version: 1.0
-- Description: Sample data for testing and development

-- Note: Passwords are BCrypt hashed version of "password123"
-- You can generate BCrypt hash using: https://bcrypt-generator.com/

-- ============================================
-- Sample Users
-- ============================================
INSERT INTO users (username, email, password_hash, phone, pin, role, is_active) VALUES
('john_doe', 'john@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '9876543210', '1234', 'USER', TRUE),
('jane_smith', 'jane@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '9876543211', '5678', 'USER', TRUE),
('admin_user', 'admin@homeheaven.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '9999999999', '0000', 'ADMIN', TRUE),
('test_user', 'test@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '9876543212', '9999', 'USER', TRUE);

-- ============================================
-- Sample Properties
-- ============================================
INSERT INTO properties (owner_id, name, address, city, property_type, rent, sqft, sharing_option, description, is_available, view_count) VALUES
(1, 'Cozy 2BHK Apartment in Andheri', '123 Main Street, Andheri West', 'Mumbai', 'Flat', 25000.00, 850, NULL, 
'Beautiful 2BHK apartment with modern amenities in the heart of Andheri. Features include spacious rooms, modular kitchen, attached bathrooms, 24/7 water supply, power backup, and covered parking. Located near metro station, shopping malls, and schools. Perfect for small families or working professionals. The apartment is well-ventilated with ample natural light. Building has lift facility and security. Rent includes maintenance charges. Available for immediate possession.', 
TRUE, 45),

(2, 'Comfortable PG for Girls in Koramangala', '456 Park Road, Koramangala 5th Block', 'Bangalore', 'PG', 8000.00, NULL, '3-sharing', 
'Safe and comfortable PG accommodation for working women and students. Fully furnished rooms with attached bathrooms, AC, WiFi, and daily housekeeping. Nutritious home-cooked meals (breakfast and dinner) included. Common amenities include TV lounge, washing machine, refrigerator, and water purifier. Located in prime Koramangala area with easy access to IT parks, restaurants, and public transport. Strict security with CCTV surveillance and biometric access. Friendly and supportive environment. No brokerage charges.', 
TRUE, 78),

(1, 'Spacious 3BHK House in Bandra', '789 Hill View, Bandra West', 'Mumbai', 'House', 45000.00, 1500, NULL, 
'Luxurious 3BHK independent house in premium Bandra locality. Features include large living room, dining area, modern kitchen with chimney and hob, three spacious bedrooms with wardrobes, three bathrooms, balcony with sea view, and private terrace. The house is semi-furnished with basic amenities. Located in a peaceful residential area with good connectivity to Western Express Highway. Nearby facilities include supermarkets, hospitals, schools, and restaurants. Suitable for families looking for comfortable living space. Pet-friendly property.', 
TRUE, 32),

(3, 'Modern Hostel for Students', '321 College Road, Near University', 'Pune', 'Hostel', 6000.00, NULL, '4-sharing', 
'Well-maintained hostel facility for college students with all modern amenities. Spacious rooms with study tables, chairs, and storage space. Common facilities include mess with hygienic food, reading room, recreation area, gym, and indoor games. High-speed WiFi available throughout the building. Regular cleaning and maintenance. Warden available 24/7 for any assistance. Located within walking distance from major colleges and universities. Safe and secure environment with CCTV monitoring. Laundry facility available. Affordable pricing with no hidden charges.', 
TRUE, 56),

(2, 'Affordable 1BHK Flat in Whitefield', '555 Tech Park Road, Whitefield', 'Bangalore', 'Flat', 15000.00, 600, NULL, 
'Compact and affordable 1BHK flat perfect for bachelors or young couples. The flat includes one bedroom, living room, kitchen, and bathroom. Semi-furnished with basic amenities like bed, wardrobe, and kitchen cabinets. Located in Whitefield area close to major IT companies and tech parks. Good connectivity via public transport and metro. Nearby amenities include supermarkets, restaurants, ATMs, and medical stores. Building has lift, power backup, and water supply. Suitable for working professionals looking for budget-friendly accommodation. Maintenance charges extra.', 
TRUE, 23),

(4, 'Premium PG for Boys in HSR Layout', '888 Sector 2, HSR Layout', 'Bangalore', 'PG', 9500.00, NULL, '2-sharing', 
'Premium PG accommodation for working professionals and students. Fully AC rooms with attached bathrooms, high-speed WiFi, and modern furniture. Includes three meals a day with varied menu options. Additional facilities include gym, TV lounge, gaming area, and rooftop terrace. Professional housekeeping and laundry services. Located in prime HSR Layout with excellent connectivity to IT corridors. Walking distance to restaurants, cafes, and shopping areas. Secure environment with biometric access and CCTV. Friendly community atmosphere. Flexible payment options available.', 
TRUE, 67);

-- ============================================
-- Sample Property Images
-- Note: These are placeholder paths. In production, these would be actual uploaded images
-- ============================================
INSERT INTO property_images (property_id, image_path, is_primary, display_order) VALUES
-- Property 1 images
(1, 'property1_main.jpg', TRUE, 0),
(1, 'property1_bedroom.jpg', FALSE, 1),
(1, 'property1_kitchen.jpg', FALSE, 2),
(1, 'property1_bathroom.jpg', FALSE, 3),

-- Property 2 images
(2, 'property2_main.jpg', TRUE, 0),
(2, 'property2_room.jpg', FALSE, 1),
(2, 'property2_common.jpg', FALSE, 2),

-- Property 3 images
(3, 'property3_main.jpg', TRUE, 0),
(3, 'property3_living.jpg', FALSE, 1),
(3, 'property3_terrace.jpg', FALSE, 2),

-- Property 4 images
(4, 'property4_main.jpg', TRUE, 0),
(4, 'property4_room.jpg', FALSE, 1),

-- Property 5 images
(5, 'property5_main.jpg', TRUE, 0),
(5, 'property5_interior.jpg', FALSE, 1),

-- Property 6 images
(6, 'property6_main.jpg', TRUE, 0),
(6, 'property6_room.jpg', FALSE, 1),
(6, 'property6_facilities.jpg', FALSE, 2);

-- ============================================
-- Sample Favorites
-- ============================================
INSERT INTO favorites (user_id, property_id) VALUES
(1, 2),
(1, 4),
(2, 1),
(2, 3),
(4, 2),
(4, 6);

-- ============================================
-- Sample Property Views (for analytics)
-- ============================================
INSERT INTO property_views (property_id, user_id, ip_address, user_agent) VALUES
(1, 2, '192.168.1.100', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/120.0.0.0'),
(1, 4, '192.168.1.101', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) Safari/605.1.15'),
(2, 1, '192.168.1.102', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) Firefox/121.0'),
(3, 2, '192.168.1.103', 'Mozilla/5.0 (iPhone; CPU iPhone OS 17_0 like Mac OS X) Safari/604.1'),
(4, 1, '192.168.1.104', 'Mozilla/5.0 (Linux; Android 13) Chrome/120.0.0.0'),
(5, 4, '192.168.1.105', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) Edge/120.0.0.0'),
(6, 2, '192.168.1.106', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) Chrome/120.0.0.0');
