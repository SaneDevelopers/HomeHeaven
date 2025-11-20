# Task 06: Testing & Quality Assurance

## 🎯 Objective
Comprehensive testing of all features, bug fixes, and quality assurance before deployment.

---

## 🧪 Testing Strategy

### 1. Unit Testing
### 2. Integration Testing
### 3. API Testing
### 4. Frontend Testing
### 5. Security Testing
### 6. Performance Testing

---

## 📋 Test Cases

### Authentication Tests

#### Registration
- [ ] Valid registration with all fields
- [ ] Registration with existing username
- [ ] Registration with existing email
- [ ] Registration with invalid email format
- [ ] Registration with password mismatch
- [ ] Registration with invalid PIN (not 4 digits)
- [ ] Registration with short password (< 6 chars)

#### Login
- [ ] Valid login credentials
- [ ] Invalid username
- [ ] Invalid password
- [ ] Login with inactive account
- [ ] JWT token generation
- [ ] Token expiration handling

#### Password Reset
- [ ] Valid email and PIN
- [ ] Invalid email
- [ ] Invalid PIN
- [ ] Password mismatch
- [ ] Short new password

---

### Property Management Tests

#### Create Property
- [ ] Valid property with all fields
- [ ] Property with images (1-10)
- [ ] Property without images
- [ ] Property with invalid rent
- [ ] Property with short description (< 100 words)
- [ ] Property without authentication
- [ ] Property with large images (> 10MB)

#### View Property
- [ ] View existing property
- [ ] View non-existent property
- [ ] View count increment
- [ ] Owner information display
- [ ] Image gallery display

#### Search Properties
- [ ] Search by city
- [ ] Search by rent range
- [ ] Search by property type
- [ ] Search with multiple filters
- [ ] Search with no results
- [ ] Search without filters (all properties)

#### My Properties
- [ ] List user's properties
- [ ] Empty list for new user
- [ ] Unauthorized access

#### Delete Property
- [ ] Delete own property
- [ ] Delete other user's property (should fail)
- [ ] Delete non-existent property
- [ ] Image cleanup after deletion

---

## 🔧 Unit Tests

### UserService Test

```java
@SpringBootTest
class UserServiceTest {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private UserRepository userRepository;
    
    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }
    
    @Test
    void testRegisterUser_Success() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setEmail("test@example.com");
        request.setPassword("password123");
        request.setConfirmPassword("password123");
        request.setPin("1234");
        
        assertDoesNotThrow(() -> userService.register(request));
        
        Optional<User> user = userRepository.findByUsername("testuser");
        assertTrue(user.isPresent());
        assertEquals("test@example.com", user.get().getEmail());
    }
    
    @Test
    void testRegisterUser_DuplicateUsername() {
        // Create first user
        RegisterRequest request1 = new RegisterRequest();
        request1.setUsername("testuser");
        request1.setEmail("test1@example.com");
        request1.setPassword("password123");
        request1.setConfirmPassword("password123");
        request1.setPin("1234");
        userService.register(request1);
        
        // Try to create second user with same username
        RegisterRequest request2 = new RegisterRequest();
        request2.setUsername("testuser");
        request2.setEmail("test2@example.com");
        request2.setPassword("password123");
        request2.setConfirmPassword("password123");
        request2.setPin("1234");
        
        assertThrows(BadRequestException.class, () -> userService.register(request2));
    }
}
```

### PropertyService Test

```java
@SpringBootTest
class PropertyServiceTest {
    
    @Autowired
    private PropertyService propertyService;
    
    @Autowired
    private PropertyRepository propertyRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    private User testUser;
    
    @BeforeEach
    void setUp() {
        propertyRepository.deleteAll();
        userRepository.deleteAll();
        
        testUser = User.builder()
                .username("testuser")
                .email("test@example.com")
                .passwordHash("hashedpassword")
                .pin("1234")
                .build();
        testUser = userRepository.save(testUser);
    }
    
    @Test
    void testCreateProperty_Success() {
        PropertyRequest request = new PropertyRequest();
        request.setName("Test Property");
        request.setAddress("123 Test St");
        request.setCity("Mumbai");
        request.setPropertyType("Flat");
        request.setRent("25000");
        request.setDescription("This is a test property with more than 100 words...");
        
        PropertyResponse response = propertyService.createProperty(
                request, 
                testUser.getUsername(), 
                null
        );
        
        assertNotNull(response.getId());
        assertEquals("Test Property", response.getName());
    }
    
    @Test
    void testSearchProperties_ByCity() {
        // Create test properties
        createTestProperty("Property 1", "Mumbai");
        createTestProperty("Property 2", "Delhi");
        createTestProperty("Property 3", "Mumbai");
        
        List<PropertyResponse> results = propertyService.searchProperties(
                "Mumbai", null, null, null
        );
        
        assertEquals(2, results.size());
    }
    
    private void createTestProperty(String name, String city) {
        Property property = Property.builder()
                .owner(testUser)
                .name(name)
                .address("Test Address")
                .city(city)
                .propertyType(Property.PropertyType.Flat)
                .rent(new BigDecimal("25000"))
                .description("Test description with more than 100 words...")
                .build();
        propertyRepository.save(property);
    }
}
```

---

## 🌐 API Testing with Postman

### Collection Structure

```
HomeHeaven API Tests
├── Authentication
│   ├── Register User
│   ├── Login User
│   ├── Reset Password
│   └── Logout
├── Properties
│   ├── Create Property
│   ├── Get Property by ID
│   ├── Search Properties
│   ├── Get My Properties
│   └── Delete Property
└── File Upload
    └── Upload Images
```

### Sample Postman Tests

**Register User**
```javascript
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Response contains success message", function () {
    pm.expect(pm.response.text()).to.include("registered successfully");
});
```

**Login User**
```javascript
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Response contains token", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData).to.have.property('token');
    pm.environment.set("jwt_token", jsonData.token);
});
```

---

## 🖥️ Frontend Testing

### Manual Test Scenarios

#### User Registration Flow
1. Navigate to `/register.html`
2. Fill all fields with valid data
3. Submit form
4. Verify success message
5. Verify redirect to login page

#### User Login Flow
1. Navigate to `/login.html`
2. Enter valid credentials
3. Submit form
4. Verify JWT token stored in localStorage
5. Verify redirect to dashboard

#### Property Upload Flow
1. Login as user
2. Navigate to `/upload-property.html`
3. Fill all property details
4. Upload 2-3 images
5. Submit form
6. Verify success message
7. Verify property appears in "My Properties"

#### Property Search Flow
1. Navigate to `/dashboard.html`
2. Enter search criteria (city, rent range, type)
3. Click search
4. Verify filtered results
5. Click on a property
6. Verify property details page

---

## 🔒 Security Testing

### Security Checklist

- [ ] SQL Injection prevention (parameterized queries)
- [ ] XSS prevention (input sanitization)
- [ ] CSRF protection
- [ ] Password encryption (BCrypt)
- [ ] JWT token validation
- [ ] Authorization checks (ownership validation)
- [ ] File upload validation (type, size)
- [ ] Rate limiting (optional)
- [ ] HTTPS in production
- [ ] Secure headers

### Security Tests

```java
@Test
void testUnauthorizedAccess_MyProperties() {
    // Try to access without token
    ResponseEntity<String> response = restTemplate.getForEntity(
            "/api/properties/my-properties",
            String.class
    );
    
    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
}

@Test
void testDeleteOtherUserProperty() {
    // User1 creates property
    // User2 tries to delete it
    // Should return 403 Forbidden
}
```

---

## ⚡ Performance Testing

### Performance Metrics

- [ ] API response time < 200ms
- [ ] Database query optimization
- [ ] Image loading optimization
- [ ] Concurrent user handling
- [ ] Memory usage monitoring

### Load Testing (Optional)

Use JMeter or Apache Bench:
```bash
ab -n 1000 -c 10 http://localhost:8080/api/properties/search
```

---

## 🐛 Bug Tracking

### Bug Report Template

```markdown
**Bug ID**: BUG-001
**Title**: Property images not displaying
**Severity**: High
**Priority**: P1
**Status**: Open

**Description**:
When viewing property details, images are not loading.

**Steps to Reproduce**:
1. Login as user
2. Navigate to property details
3. Observe image section

**Expected Result**:
Images should display

**Actual Result**:
Broken image icons

**Environment**:
- Browser: Chrome 120
- OS: Windows 11
- Backend: Spring Boot 3.3.5

**Fix**:
Update image path in PropertyResponse
```

---

## ✅ Testing Checklist

### Backend
- [ ] All unit tests passing
- [ ] All integration tests passing
- [ ] API endpoints tested with Postman
- [ ] Security tests passing
- [ ] Error handling tested
- [ ] Validation working correctly

### Frontend
- [ ] All pages loading correctly
- [ ] Forms submitting properly
- [ ] Error messages displaying
- [ ] Success messages displaying
- [ ] Navigation working
- [ ] Responsive design tested
- [ ] Cross-browser testing (Chrome, Firefox, Safari)

### Integration
- [ ] Frontend-backend communication working
- [ ] JWT authentication working
- [ ] File upload working
- [ ] Image display working
- [ ] Search functionality working
- [ ] CRUD operations working

### Performance
- [ ] Page load time < 3 seconds
- [ ] API response time < 200ms
- [ ] Image optimization
- [ ] Database queries optimized

---

## 📊 Test Coverage

Target: 80%+ code coverage

```bash
# Run tests with coverage
mvn clean test jacoco:report

# View coverage report
open target/site/jacoco/index.html
```

---

**Status**: ⏳ Pending
**Estimated Time**: 8 hours
**Dependencies**: TASK_05 (Integration)
**Next Task**: TASK_07_DEPLOYMENT.md
