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
  
  // Get email
  getEmail() {
    return localStorage.getItem('email');
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
    
    if (token && !options.headers['Authorization']) {
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

// Display username in navbar
function displayUsername() {
  const username = Auth.getUsername();
  if (username) {
    const userElements = document.querySelectorAll('.username-display');
    userElements.forEach(el => {
      el.textContent = username;
    });
  }
}
