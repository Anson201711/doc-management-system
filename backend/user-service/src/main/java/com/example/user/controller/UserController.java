package com.example.user.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.user.dto.UserCreateDTO;
import com.example.user.dto.UserResponseDTO;
import com.example.user.dto.UserUpdateDTO;
import com.example.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    
    @PostMapping
    public ResponseEntity<Map<String, Object>> createUser(@Valid @RequestBody UserCreateDTO dto) {
        UserResponseDTO user = userService.createUser(dto);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "用户创建成功");
        response.put("data", user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateUser(@PathVariable Long id, @RequestBody UserUpdateDTO dto) {
        dto.setId(id);
        UserResponseDTO user = userService.updateUser(dto);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "用户更新成功");
        response.put("data", user);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "用户删除成功");
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable Long id) {
        UserResponseDTO user = userService.getUserById(id);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", user);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/username/{username}")
    public ResponseEntity<Map<String, Object>> getUserByUsername(@PathVariable String username) {
        UserResponseDTO user = userService.getUserByUsername(username);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", user);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllUsers() {
        List<UserResponseDTO> users = userService.getAllUsers();
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", users);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/page")
    public ResponseEntity<Map<String, Object>> getUsersPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        IPage<UserResponseDTO> page = userService.getUsersPage(pageNum, pageSize);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", page);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/check/{username}")
    public ResponseEntity<Map<String, Object>> checkUsername(@PathVariable String username) {
        boolean exists = userService.existsByUsername(username);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("exists", exists);
        return ResponseEntity.ok(response);
    }
}