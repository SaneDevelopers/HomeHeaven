# Task 05: Frontend-Backend Integration

## 🎯 Objective
Connect the existing frontend (red/white theme) to the new backend API with proper error handling and authentication flow.

---

## 🔄 API Endpoint Mapping

### Current Frontend → New Backend

| Frontend File | Current Endpoint | New API Endpoint | Method |
|--------------|------------------|------------------|--------|
| login.html | `/auth/login` | `/api/auth/login` | POST |
| register.html | `/auth/register` | `/api/auth/register` | POST |
| forgot-password.html | `/auth/forgot-password` | `/api/auth/forgot-password` | POST |
| dashboard.html | `/properties/search` | `/api/properties/search` | GET |
| property-details.html | `/properties/{id}` | `/api/properties/{id}` | GET |
| upload-property.html | `/properties/upload` | `/api/properties/upload` | POST |
| admin.html | `/admin/my-properties` | `/api/properties/my-properties` | GET |
| admin.html | `/admin/property/{id}` | `/api/properties/{id}` | DELETE |

---

## 🔐 Authentication Flow

### 1. Update Login Page

**File**: `src/main/resources/static/login.html`

Update the login script to handle JWT tokens:

```javascript
document.getElementById('loginForm').onsubmit = async (e) => {
  e.preventDefault();
  const submitBtn = e.target.querySelector('button[type="submit"]');
  const msgDiv = document.getElementById('msg');
  
  submitBtn.disabled = true;
  submitBtn.innerHTML = '<i class="fa-solid fa-spinner fa-spin"></i> Signing in...';
  msgDiv.style.display = 'none';

  const data = new FormData(e.target);
  const body = {
    username: data.get('username'),
    password: data.get('password')
  };

  try {
    const res = await fetch('/api/auth/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(body)
    });

    if (res.ok) {
      const response = await res.json();
      
      // Store JWT token in localStorage
      localStorage.setItem('token', response.token);
      localStorage.setItem('username', response.username);
      localStorage.setItem('email', response.email);
      
      msgDiv.className = 'message success';
      msgDiv.innerHTML = '<i class="fa-solid fa-circle-check"></i> Login successful! Redirecting...';
      msgDiv.style.display = 'flex';
      
      setTimeout(() => window.location = '/dashboard.html', 1000);
    } else {
      const error = await res.text();
      msgDiv.className = 'message error';
      msgDiv.innerHTML = '<i class="fa-solid fa-circle-xmark"></i> ' + error;
      msgDiv.style.display = 'flex';
      submitBtn.disabled = false;
      submitBtn.innerHTML = '<i class="fa-solid fa-right-to-bracket"></i> Sign In';
    }
  } catch (err) {
    msgDiv.className = 'message error';
    msgDiv.innerHTML = '<i class="fa-solid fa-circle-xmark"></i> Connection error. Please try again.';
    msgDiv.style.display = 'flex';
    submitBtn.disabled = false;
    submitBtn.innerHTML = '<i class="fa-solid fa-right-to-bracket"></i> Sign In';
  }
};
```

### 2. Create Auth Utility

**File**: `src/main/resources/static/js/auth.js`

```javascript
// Authentication utility functions
const Auth = {
  // Get token from localStorage
  getToken() {
    return localStorage.getItem('token');
  },
  
  // Get username
  getUsername() {
    return localStorage.getItem('username');
  },
  
  // Check if user is authenticated
  isAuthenticated() {
    return !!this.getToken();
  },
  
  // Logout user
  logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('username');
    localStorage.removeItem('email');
    window.location = '/login.html';
  },
  
  // Get authorization headers
  getHeaders() {
    const token = this.getToken();
    return {
      'Content-Type': 'application/json',
      'Authorization': token ? `Bearer ${token}` : ''
    };
  },
  
  // Make authenticated API call
  async fetch(url, options = {}) {
    const token = this.getToken();
    
    if (!options.headers) {
      options.headers = {};
    }
    
    if (token) {
      options.headers['Authorization'] = `Bearer ${token}`;
    }
    
    const response = await fetch(url, options);
    
    // If unauthorized, redirect to login
    if (response.status === 401) {
      this.logout();
      return;
    }
    
    return response;
  }
};

// Check authentication on protected pages
function requireAuth() {
  if (!Auth.isAuthenticated()) {
    window.location = '/login.html';
  }
}
```

### 3. Update Dashboard

**File**: `src/main/resources/static/dashboard.html`

Add auth check and update API calls:

```javascript
// Add at the top of script section
requireAuth();

// Update renderResults function
async function renderResults(q = '') {
  const res = await Auth.fetch('/api/properties/search' + q);
  const data = await res.json();
  // ... rest of the code
}

// Update logout
document.getElementById('logoutLink').onclick = async (e) => {
  e.preventDefault();
  await Auth.fetch('/api/auth/logout', { method: 'POST' });
  Auth.logout();
};
```

### 4. Update Upload Property

**File**: `src/main/resources/static/upload-property.html`

Update form submission:

```javascript
document.getElementById('uploadForm').onsubmit = async (e) => {
  e.preventDefault();
  const submitBtn = e.target.querySelector('button[type="submit"]');
  
  submitBtn.disabled = true;
  submitBtn.innerHTML = '<i class="fa-solid fa-spinner fa-spin"></i> Uploading...';

  const formData = new FormData(e.target);
  
  try {
    const token = Auth.getToken();
    const res = await fetch('/api/properties/upload', {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${token}`
      },
      body: formData
    });

    if (res.ok) {
      document.getElementById('msg').innerHTML = `
        <div class="message success">
          <i class="fa-solid fa-circle-check"></i> 
          Property uploaded successfully!
          <br><a href="/dashboard.html"><i class="fa-solid fa-arrow-left"></i> Back to Dashboard</a>
        </div>
      `;
      document.getElementById('uploadForm').style.display = 'none';
    } else {
      const error = await res.text();
      document.getElementById('msg').innerHTML = `
        <div class="message error">
          <i class="fa-solid fa-circle-xmark"></i> ${error}
        </div>
      `;
      submitBtn.disabled = false;
      submitBtn.innerHTML = '<i class="fa-solid fa-upload"></i> Upload Property';
    }
  } catch (error) {
    document.getElementById('msg').innerHTML = `
      <div class="message error">
        <i class="fa-solid fa-circle-xmark"></i> An error occurred. Please try again.
      </div>
    `;
    submitBtn.disabled = false;
    submitBtn.innerHTML = '<i class="fa-solid fa-upload"></i> Upload Property';
  }
};
```

---

## 📝 Update All Frontend Files

### Files to Update:
1. ✅ login.html - Add JWT token storage
2. ✅ register.html - Update API endpoint
3. ✅ forgot-password.html - Update API endpoint
4. ✅ dashboard.html - Add auth check, update API calls
5. ✅ property-details.html - Update API endpoint
6. ✅ upload-property.html - Add auth header, update endpoint
7. ✅ admin.html - Add auth check, update API calls

---

## 🔧 CORS Configuration

**File**: `src/main/java/com/homeheaven/config/WebConfig.java`

```java
package com.homeheaven.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("Authorization")
                .maxAge(3600);
    }
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:./uploads/");
    }
}
```

---

## ✅ Integration Checklist

### Backend
- [ ] Update SecurityConfig to allow frontend endpoints
- [ ] Add CORS configuration
- [ ] Test all API endpoints with Postman
- [ ] Verify JWT token generation
- [ ] Test file upload functionality

### Frontend
- [ ] Create auth.js utility
- [ ] Update login.html
- [ ] Update register.html
- [ ] Update forgot-password.html
- [ ] Update dashboard.html
- [ ] Update property-details.html
- [ ] Update upload-property.html
- [ ] Update admin.html
- [ ] Add auth checks to protected pages
- [ ] Test complete user flow

### Testing
- [ ] Test registration flow
- [ ] Test login flow
- [ ] Test password reset
- [ ] Test property upload
- [ ] Test property search
- [ ] Test property details
- [ ] Test my properties
- [ ] Test property deletion
- [ ] Test logout
- [ ] Test unauthorized access

---

## 🐛 Common Issues & Solutions

### Issue 1: CORS Errors
**Solution**: Ensure CORS is properly configured in WebConfig

### Issue 2: 401 Unauthorized
**Solution**: Check if JWT token is being sent in Authorization header

### Issue 3: File Upload Fails
**Solution**: Ensure multipart/form-data is used and file size limits are configured

### Issue 4: Token Expiration
**Solution**: Implement token refresh or redirect to login on 401

---

**Status**: ⏳ Pending
**Estimated Time**: 6 hours
**Dependencies**: TASK_04 (Property Management)
**Next Task**: TASK_06_TESTING.md
