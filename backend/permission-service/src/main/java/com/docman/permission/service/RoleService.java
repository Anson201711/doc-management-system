package com.docman.permission.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.docman.permission.dto.RoleDTO;
import com.docman.permission.entity.Role;
import com.docman.permission.entity.RolePermission;
import com.docman.permission.repository.RolePermissionRepository;
import com.docman.permission.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色管理服务
 */
@Service
@RequiredArgsConstructor
public class RoleService {
    
    private final RoleRepository roleRepository;
    private final RolePermissionRepository rolePermissionRepository;
    
    /**
     * 分页查询角色列表
     */
    public Page<Role> findPage(int pageNum, int pageSize, String name) {
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        if (name != null && !name.isEmpty()) {
            wrapper.like(Role::getName, name);
        }
        wrapper.orderByDesc(Role::getCreatedAt);
        return roleRepository.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }
    
    /**
     * 根据ID查询角色
     */
    public Role findById(Long id) {
        return roleRepository.selectById(id);
    }
    
    /**
     * 查询所有角色
     */
    public List<Role> findAll() {
        return roleRepository.selectList(null);
    }
    
    /**
     * 创建角色
     */
    @Transactional
    public Role create(RoleDTO dto) {
        Role role = new Role();
        role.setName(dto.getName());
        role.setDescription(dto.getDescription());
        role.setStatus(dto.getStatus() != null ? dto.getStatus() : "active");
        role.setCreatedAt(LocalDateTime.now());
        role.setUpdatedAt(LocalDateTime.now());
        roleRepository.insert(role);
        
        // 关联权限
        if (dto.getPermissionIds() != null && !dto.getPermissionIds().isEmpty()) {
            for (Long permissionId : dto.getPermissionIds()) {
                RolePermission rp = new RolePermission();
                rp.setRoleId(role.getId());
                rp.setPermissionId(permissionId);
                rp.setCreatedAt(LocalDateTime.now());
                rolePermissionRepository.insert(rp);
            }
        }
        return role;
    }
    
    /**
     * 更新角色
     */
    @Transactional
    public Role update(Long id, RoleDTO dto) {
        Role role = roleRepository.selectById(id);
        if (role == null) {
            throw new RuntimeException("角色不存在");
        }
        role.setName(dto.getName());
        role.setDescription(dto.getDescription());
        if (dto.getStatus() != null) {
            role.setStatus(dto.getStatus());
        }
        role.setUpdatedAt(LocalDateTime.now());
        roleRepository.updateById(role);
        
        // 更新权限关联
        if (dto.getPermissionIds() != null) {
            // 删除旧关联
            LambdaQueryWrapper<RolePermission> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(RolePermission::getRoleId, id);
            rolePermissionRepository.delete(wrapper);
            
            // 添加新关联
            for (Long permissionId : dto.getPermissionIds()) {
                RolePermission rp = new RolePermission();
                rp.setRoleId(id);
                rp.setPermissionId(permissionId);
                rp.setCreatedAt(LocalDateTime.now());
                rolePermissionRepository.insert(rp);
            }
        }
        return role;
    }
    
    /**
     * 删除角色
     */
    @Transactional
    public void delete(Long id) {
        // 删除角色权限关联
        LambdaQueryWrapper<RolePermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RolePermission::getRoleId, id);
        rolePermissionRepository.delete(wrapper);
        
        roleRepository.deleteById(id);
    }
    
    /**
     * 获取角色关联的权限ID列表
     */
    public List<Long> getPermissionIds(Long roleId) {
        return rolePermissionRepository.selectPermissionIdsByRoleId(roleId);
    }
}