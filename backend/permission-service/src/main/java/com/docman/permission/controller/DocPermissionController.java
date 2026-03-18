package com.docman.permission.controller;

import com.docman.permission.dto.DocPermissionDTO;
import com.docman.permission.dto.Result;
import com.docman.permission.entity.DocPermission;
import com.docman.permission.service.DocPermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 文档权限Controller - 文档级别的细粒度权限控制
 */
@RestController
@RequestMapping("/api/v1/documents/{documentId}/permissions")
@RequiredArgsConstructor
@Tag(name = "文档权限管理", description = "文档权限CRUD接口")
public class DocPermissionController {
    
    private final DocPermissionService docPermissionService;
    
    @GetMapping
    @Operation(summary = "获取文档的所有权限")
    public Result<List<DocPermission>> list(@PathVariable Long documentId) {
        return Result.success(docPermissionService.findByDocumentId(documentId));
    }
    
    @GetMapping("/user/{userId}")
    @Operation(summary = "获取用户在文档上的权限")
    public Result<List<DocPermission>> getUserPermission(
            @PathVariable Long documentId,
            @PathVariable Long userId) {
        return Result.success(docPermissionService.findByDocumentIdAndUserId(documentId, userId));
    }
    
    @PostMapping
    @Operation(summary = "添加文档权限")
    public Result<DocPermission> add(
            @PathVariable Long documentId,
            @RequestBody DocPermissionDTO dto) {
        dto.setDocumentId(documentId);
        DocPermission permission;
        if (dto.getUserId() != null) {
            permission = docPermissionService.addUserPermission(dto);
        } else if (dto.getRoleId() != null) {
            permission = docPermissionService.addRolePermission(dto);
        } else {
            return Result.error("必须指定用户或角色");
        }
        return Result.success(permission);
    }
    
    @PostMapping("/batch/user")
    @Operation(summary = "批量添加用户权限")
    public Result<List<DocPermission>> batchAddUserPermissions(
            @PathVariable Long documentId,
            @RequestBody DocPermissionDTO dto) {
        dto.setDocumentId(documentId);
        return Result.success(docPermissionService.batchAddUserPermissions(dto));
    }
    
    @PostMapping("/batch/role")
    @Operation(summary = "批量添加角色权限")
    public Result<List<DocPermission>> batchAddRolePermissions(
            @PathVariable Long documentId,
            @RequestBody DocPermissionDTO dto) {
        dto.setDocumentId(documentId);
        return Result.success(docPermissionService.batchAddRolePermissions(dto));
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "更新文档权限")
    public Result<DocPermission> update(
            @PathVariable Long documentId,
            @PathVariable Long id,
            @RequestBody DocPermissionDTO dto) {
        return Result.success(docPermissionService.update(id, dto));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "删除文档权限")
    public Result<Void> delete(
            @PathVariable Long documentId,
            @PathVariable Long id) {
        docPermissionService.delete(id);
        return Result.success(null);
    }
    
    @DeleteMapping
    @Operation(summary = "删除文档的所有权限")
    public Result<Void> deleteAll(@PathVariable Long documentId) {
        docPermissionService.deleteByDocumentId(documentId);
        return Result.success(null);
    }
}