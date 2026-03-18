package com.docman.permission.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.docman.permission.dto.PermissionDTO;
import com.docman.permission.entity.Permission;
import com.docman.permission.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 权限管理服务
 */
@Service
@RequiredArgsConstructor
public class PermissionService {
    
    private final PermissionRepository permissionRepository;
    
    /**
     * 分页查询权限列表
     */
    public Page<Permission> findPage(int pageNum, int pageSize, String name, String resource) {
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        if (name != null && !name.isEmpty()) {
            wrapper.like(Permission::getName, name);
        }
        if (resource != null && !resource.isEmpty()) {
            wrapper.eq(Permission::getResource, resource);
        }
        wrapper.orderByDesc(Permission::getCreatedAt);
        return permissionRepository.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }
    
    /**
     * 根据ID查询权限
     */
    public Permission findById(Long id) {
        return permissionRepository.selectById(id);
    }
    
    /**
     * 查询所有权限
     */
    public List<Permission> findAll() {
        return permissionRepository.selectList(null);
    }
    
    /**
     * 根据资源查询权限
     */
    public List<Permission> findByResource(String resource) {
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Permission::getResource, resource);
        return permissionRepository.selectList(wrapper);
    }
    
    /**
     * 创建权限
     */
    public Permission create(PermissionDTO dto) {
        Permission permission = new Permission();
        permission.setName(dto.getName());
        permission.setResource(dto.getResource());
        permission.setAction(dto.getAction());
        permission.setDescription(dto.getDescription());
        permission.setCreatedAt(LocalDateTime.now());
        permission.setUpdatedAt(LocalDateTime.now());
        permissionRepository.insert(permission);
        return permission;
    }
    
    /**
     * 更新权限
     */
    public Permission update(Long id, PermissionDTO dto) {
        Permission permission = permissionRepository.selectById(id);
        if (permission == null) {
            throw new RuntimeException("权限不存在");
        }
        permission.setName(dto.getName());
        permission.setResource(dto.getResource());
        permission.setAction(dto.getAction());
        permission.setDescription(dto.getDescription());
        permission.setUpdatedAt(LocalDateTime.now());
        permissionRepository.updateById(permission);
        return permission;
    }
    
    /**
     * 删除权限
     */
    @Transactional
    public void delete(Long id) {
        permissionRepository.deleteById(id);
    }
}