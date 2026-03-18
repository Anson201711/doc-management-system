package com.example.permission.controller;

import com.example.permission.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/permissions")
@RequiredArgsConstructor
public class PermissionController {
    
    private final PermissionService permissionService;
    
    @GetMapping("/check")
    public ResponseEntity<Map<String, Object>> checkPermission(
            @RequestParam Long userId,
            @RequestParam String resource,
            @RequestParam String action) {
        boolean hasPermission = permissionService.hasPermission(userId, resource, action);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("hasPermission", hasPermission);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/role")
    public ResponseEntity<Map<String, Object>> assignRole(
            @RequestParam Long userId,
            @RequestParam String role) {
        permissionService.assignRole(userId, role);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "角色分配成功");
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/role")
    public ResponseEntity<Map<String, Object>> revokeRole(
            @RequestParam Long userId,
            @RequestParam String role) {
        permissionService.revokeRole(userId, role);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "角色撤销成功");
        return ResponseEntity.ok(response);
    }
}