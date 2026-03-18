package com.docman.permission.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.docman.permission.dto.UserRoleDTO;
import com.docman.permission.entity.UserRole;
import com.docman.permission.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户角色关联服务
 */
@Service
@RequiredArgsConstructor
public class UserRoleService {
    
    private final UserRoleRepository userRoleRepository;
    
    /**
     * 根据用户ID查询角色ID列表
     */
    public List<Long> findRoleIdsByUserId(Long userId) {
        return userRoleRepository.selectRoleIdsByUserId(userId);
    }
    
    /**
     * 根据用户ID查询角色列表
     */
    public List<UserRole> findByUserId(Long userId) {
        LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRole::getUserId, userId);
        return userRoleRepository.selectList(wrapper);
    }
    
    /**
     * 分配角色给用户
     */
    public UserRole assign(UserRoleDTO dto) {
        // 检查是否已存在
        LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRole::getUserId, dto.getUserId())
               .eq(UserRole::getRoleId, dto.getRoleId());
        UserRole existing = userRoleRepository.selectOne(wrapper);
        if (existing != null) {
            return existing;
        }
        
        UserRole userRole = new UserRole();
        userRole.setUserId(dto.getUserId());
        userRole.setRoleId(dto.getRoleId());
        userRole.setCreatedAt(LocalDateTime.now());
        userRoleRepository.insert(userRole);
        return userRole;
    }
    
    /**
     * 移除用户的角色
     */
    @Transactional
    public void remove(Long userId, Long roleId) {
        LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRole::getUserId, userId)
               .eq(UserRole::getRoleId, roleId);
        userRoleRepository.delete(wrapper);
    }
    
    /**
     * 移除用户的所有角色
     */
    @Transactional
    public void removeAllByUserId(Long userId) {
        LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRole::getUserId, userId);
        userRoleRepository.delete(wrapper);
    }
}