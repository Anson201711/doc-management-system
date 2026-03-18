package com.docman.permission.controller;

import com.docman.permission.dto.Result;
import com.docman.permission.dto.UserRoleDTO;
import com.docman.permission.entity.UserRole;
import com.docman.permission.service.UserRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户角色关联Controller
 */
@RestController
@RequestMapping("/api/v1/user-roles")
@RequiredArgsConstructor
@Tag(name = "用户角色管理", description = "用户与角色关联接口")
public class UserRoleController {
    
    private final UserRoleService userRoleService;
    
    @GetMapping("/user/{userId}")
    @Operation(summary = "获取用户的所有角色")
    public Result<List<UserRole>> getByUserId(@PathVariable Long userId) {
        return Result.success(userRoleService.findByUserId(userId));
    }
    
    @GetMapping("/role/{roleId}")
    @Operation(summary = "获取角色的所有用户")
    public Result<List<UserRole>> getByRoleId(@PathVariable Long roleId) {
        return Result.success(userRoleService.findByRoleId(roleId));
    }
    
    @PostMapping
    @Operation(summary = "分配角色给用户")
    public Result<UserRole> assign(@RequestBody UserRoleDTO dto) {
        UserRole userRole = userRoleService.assign(dto);
        return Result.success(userRole);
    }
    
    @DeleteMapping
    @Operation(summary = "移除用户的角色")
    public Result<Void> remove(@RequestBody UserRoleDTO dto) {
        userRoleService.remove(dto);
        return Result.success(null);
    }
    
    @DeleteMapping("/user/{userId}/role/{roleId}")
    @Operation(summary = "移除用户的指定角色")
    public Result<Void> removeUserRole(
            @PathVariable Long userId,
            @PathVariable Long roleId) {
        userRoleService.removeByUserIdAndRoleId(userId, roleId);
        return Result.success(null);
    }
}