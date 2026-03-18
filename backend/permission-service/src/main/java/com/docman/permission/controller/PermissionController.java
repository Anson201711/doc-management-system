package com.docman.permission.controller;

import com.docman.permission.dto.PermissionDTO;
import com.docman.permission.dto.Result;
import com.docman.permission.entity.Permission;
import com.docman.permission.service.PermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 权限管理Controller
 */
@RestController
@RequestMapping("/api/v1/permissions")
@RequiredArgsConstructor
@Tag(name = "权限管理", description = "权限CRUD接口")
public class PermissionController {
    
    private final PermissionService permissionService;
    
    @GetMapping
    @Operation(summary = "获取权限列表")
    public Result<List<Permission>> list() {
        return Result.success(permissionService.findAll());
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "获取权限详情")
    public Result<Permission> getById(@PathVariable Long id) {
        Permission permission = permissionService.findById(id);
        if (permission == null) {
            return Result.error("权限不存在");
        }
        return Result.success(permission);
    }
    
    @PostMapping
    @Operation(summary = "创建权限")
    public Result<Permission> create(@RequestBody PermissionDTO dto) {
        Permission permission = permissionService.create(dto);
        return Result.success(permission);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "更新权限")
    public Result<Permission> update(@PathVariable Long id, @RequestBody PermissionDTO dto) {
        Permission permission = permissionService.update(id, dto);
        return Result.success(permission);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "删除权限")
    public Result<Void> delete(@PathVariable Long id) {
        permissionService.delete(id);
        return Result.success(null);
    }
}