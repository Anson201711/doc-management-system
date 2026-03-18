package com.docman.user.controller;

import com.docman.user.dto.*;
import com.docman.user.security.JwtTokenProvider;
import com.docman.user.service.AuthService;
import com.docman.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "认证管理", description = "用户登录、注册、登出等认证接口")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "使用用户名和密码登录，返回JWT令牌")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest request) {
        try {
            String token = authService.login(request.getUsername(), request.getPassword());
            LoginResponse response = new LoginResponse();
            response.setToken(token);
            response.setTokenType("Bearer");
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "注册新用户")
    public ResponseEntity<ApiResponse<String>> register(@RequestBody RegisterRequest request) {
        try {
            userService.register(
                request.getUsername(),
                request.getEmail(),
                request.getPassword(),
                request.getFullName()
            );
            return ResponseEntity.ok(ApiResponse.success("注册成功"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/logout")
    @Operation(summary = "用户登出", description = "退出登录并将Token加入黑名单")
    public ResponseEntity<ApiResponse<String>> logout(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            Authentication authentication) {
        try {
            if (authentication != null && authentication.getPrincipal() != null) {
                Long userId = (Long) authentication.getPrincipal();
                String token = null;
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    token = authHeader.substring(7);
                }
                authService.logout(token, userId);
            }
            return ResponseEntity.ok(ApiResponse.success("登出成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/refresh")
    @Operation(summary = "刷新Token", description = "刷新JWT令牌")
    public ResponseEntity<ApiResponse<LoginResponse>> refresh(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            String newToken = authService.refreshToken(token);
            LoginResponse response = new LoginResponse();
            response.setToken(newToken);
            response.setTokenType("Bearer");
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/me")
    @Operation(summary = "获取当前用户", description = "获取当前登录用户的信息")
    public ResponseEntity<ApiResponse<UserInfo>> getCurrentUser(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("未登录"));
        }
        
        Long userId = (Long) authentication.getPrincipal();
        var user = authService.getCurrentUser(userId);
        
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
}