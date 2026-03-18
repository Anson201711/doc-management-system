package com.docman.permission.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.docman.permission.dto.RoleDTO;
import com.docman.permission.dto.Result;
import com.docman.permission.entity.Role;
import com.docman.permission.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色管理Controller
 */
@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
@Tag(name = "角色管理", description = "角色CRUD接口")
public class RoleController {
    
    private final RoleService roleService;
    
    @GetMapping
    @Operation(summary = "获取角色列表", description = "分页查询角色")
    public Result<Page<Role>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String name) {
        return Result.success(roleService.findPage(pageNum, pageSize, name));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "获取角色详情")
    public Result<Role> getById(@PathVariable Long id) {
        Role role = roleService.findById(id);
        if (role == null) {
            return Result.error("角色不存在");
        }
        return Result.success(role);
    }
    
    @GetMapping("/all")
    @Operation(summary = "获取所有角色")
    public Result<List<Role>> getAll() {
        return Result.success(roleService.findAll());
    }
    
    @PostMapping
    @Operation(summary = "创建角色")
    public Result<Role> create(@RequestBody RoleDTO dto) {
        Role role = roleService.create(dto);
        return Result.success(role);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "更新角色")
    public Result<Role> update(@PathVariable Long id, @RequestBody RoleDTO dto) {
        Role role = roleService.update(id, dto);
        return Result.success(role);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "删除角色")
    public Result<Void> delete(@PathVariable Long id) {
        roleService.delete(id);
        return Result.success(null);
    }
    
    @GetMapping("/{id}/permissions")
    @Operation(summary = "获取角色关联的权限")
    public Result<List<Long>> getPermissionIds(@PathVariable Long id) {
        return Result.success(roleService.getPermissionIds(id));
    }
}