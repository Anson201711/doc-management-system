package com.docman.user.controller;

import com.docman.user.dto.*;
import com.docman.user.entity.User;
import com.docman.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理控制器
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "用户管理", description = "用户信息查询、修改等接口")
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    @Operation(summary = "获取用户信息", description = "根据ID获取用户详细信息")
    public ResponseEntity<ApiResponse<UserInfo>> getUserById(@PathVariable Long id) {
        User user = userService.findById(id);
        if (user == null) {
            return ResponseEntity.badRequest().body(ApiResponse.error("用户不存在"));
        }
        
        UserInfo userInfo = new UserInfo();
        userInfo.setId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setEmail(user.getEmail());
        userInfo.setFullName(user.getFullName());
        userInfo.setAvatarUrl(user.getAvatarUrl());
        userInfo.setPhone(user.getPhone());
        userInfo.setDepartmentId(user.getDepartmentId());
        userInfo.setStatus(user.getStatus());
        
        return ResponseEntity.ok(ApiResponse.success(userInfo));
    }

    @PutMapping("/profile")
    @Operation(summary = "更新用户信息", description = "更新当前登录用户的信息")
    public ResponseEntity<ApiResponse<UserInfo>> updateProfile(
            @RequestBody UserUpdateRequest request,
            Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("未登录"));
        }
        
        Long userId = (Long) authentication.getPrincipal();
        
        try {
            User user = userService.updateUser(
                userId,
                request.getFullName(),
                request.getAvatarUrl(),
                request.getPhone(),
                request.getDepartmentId()
            );
            
            UserInfo userInfo = new UserInfo();
            userInfo.setId(user.getId());
            userInfo.setUsername(user.getUsername());
            userInfo.setEmail(user.getEmail());
            userInfo.setFullName(user.getFullName());
            userInfo.setAvatarUrl(user.getAvatarUrl());
            userInfo.setPhone(user.getPhone());
            userInfo.setDepartmentId(user.getDepartmentId());
            userInfo.setStatus(user.getStatus());
            
            return ResponseEntity.ok(ApiResponse.success(userInfo));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/password")
    @Operation(summary = "修改密码", description = "修改当前登录用户的密码")
    public ResponseEntity<ApiResponse<String>> changePassword(
            @RequestBody ChangePasswordRequest request,
            Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("未登录"));
        }
        
        Long userId = (Long) authentication.getPrincipal();
        
        try {
            userService.updatePassword(userId, request.getOldPassword(), request.getNewPassword());
            return ResponseEntity.ok(ApiResponse.success("密码修改成功"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/exists/username/{username}")
    @Operation(summary = "检查用户名", description = "检查用户名是否已存在")
    public ResponseEntity<ApiResponse<Boolean>> checkUsername(@PathVariable String username) {
        boolean exists = userService.existsByUsername(username);
        return ResponseEntity.ok(ApiResponse.success(exists));
    }

    @GetMapping("/exists/email/{email}")
    @Operation(summary = "检查邮箱", description = "检查邮箱是否已存在")
    public ResponseEntity<ApiResponse<Boolean>> checkEmail(@PathVariable String email) {
        boolean exists = userService.existsByEmail(email);
        return ResponseEntity.ok(ApiResponse.success(exists));
    }
}