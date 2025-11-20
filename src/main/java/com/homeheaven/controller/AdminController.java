package com.homeheaven.controller;

import com.homeheaven.dto.response.PropertyResponse;
import com.homeheaven.dto.response.UserResponse;
import com.homeheaven.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    
    private final AdminService adminService;
    
    @GetMapping("/dashboard/stats")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        log.info("GET /api/admin/dashboard/stats");
        Map<String, Object> stats = adminService.getDashboardStats();
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        log.info("GET /api/admin/users");
        List<UserResponse> users = adminService.getAllUsers();
        return ResponseEntity.ok(users);
    }
    
    @PutMapping("/users/{id}/block")
    public ResponseEntity<String> blockUser(@PathVariable Long id) {
        log.info("PUT /api/admin/users/{}/block", id);
        adminService.blockUser(id);
        return ResponseEntity.ok("User blocked successfully");
    }
    
    @PutMapping("/users/{id}/unblock")
    public ResponseEntity<String> unblockUser(@PathVariable Long id) {
        log.info("PUT /api/admin/users/{}/unblock", id);
        adminService.unblockUser(id);
        return ResponseEntity.ok("User unblocked successfully");
    }
    
    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        log.info("DELETE /api/admin/users/{}", id);
        adminService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully");
    }
    
    @GetMapping("/properties")
    public ResponseEntity<List<PropertyResponse>> getAllProperties() {
        log.info("GET /api/admin/properties");
        List<PropertyResponse> properties = adminService.getAllProperties();
        return ResponseEntity.ok(properties);
    }
    
    @DeleteMapping("/properties/{id}")
    public ResponseEntity<String> deleteProperty(@PathVariable Long id) {
        log.info("DELETE /api/admin/properties/{}", id);
        adminService.deleteProperty(id);
        return ResponseEntity.ok("Property deleted successfully");
    }
    
    @PutMapping("/properties/{id}/toggle-availability")
    public ResponseEntity<String> togglePropertyAvailability(@PathVariable Long id) {
        log.info("PUT /api/admin/properties/{}/toggle-availability", id);
        adminService.togglePropertyAvailability(id);
        return ResponseEntity.ok("Property availability toggled");
    }
}
