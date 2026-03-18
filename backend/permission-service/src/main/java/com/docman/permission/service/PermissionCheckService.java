package com.docman.permission.service;

import com.docman.permission.entity.DocPermission;
import com.docman.permission.entity.Permission;
import com.docman.permission.entity.RolePermission;
import com.docman.permission.entity.UserRole;
import com.docman.permission.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 权限校验服务
 */
@Service
@RequiredArgsConstructor
public class PermissionCheckService {
    
    private final UserRoleRepository userRoleRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final PermissionRepository permissionRepository;
    private final DocPermissionRepository docPermissionRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    
    private static final String PERMISSION_CACHE_PREFIX = "perm:user:";
    private static final String DOC_PERM_CACHE_PREFIX = "perm:doc:";
    private static final long CACHE_EXPIRE_MINUTES = 30;
    
    /**
     * 获取用户的所有权限
     */
    public List<Permission> getUserPermissions(Long userId) {
        // 先从缓存获取
        String cacheKey = PERMISSION_CACHE_PREFIX + userId;
        List<Permission> cached = (List<Permission>) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return cached;
        }
        
        // 获取用户角色
        List<Long> roleIds = userRoleRepository.selectRoleIdsByUserId(userId);
        if (roleIds.isEmpty()) {
            return List.of();
        }
        
        // 获取角色权限
        List<Long> permissionIds = roleIds.stream()
                .map(roleId -> rolePermissionRepository.selectPermissionIdsByRoleId(roleId))
                .flatMap(List::stream)
                .distinct()
                .collect(Collectors.toList());
        
        if (permissionIds.isEmpty()) {
            return List.of();
        }
        
        // 查询权限详情
        List<Permission> permissions = permissionRepository.selectBatchIds(permissionIds);
        
        // 缓存结果
        redisTemplate.opsForValue().set(cacheKey, permissions, CACHE_EXPIRE_MINUTES, TimeUnit.MINUTES);
        
        return permissions;
    }
    
    /**
     * 检查用户是否具有指定权限
     */
    public boolean checkPermission(Long userId, String resource, String action) {
        List<Permission> permissions = getUserPermissions(userId);
        
        return permissions.stream().anyMatch(p -> 
            p.getResource().equals(resource) && p.getAction().equals(action));
    }
    
    /**
     * 检查用户是否具有文档访问权限
     */
    public boolean hasDocumentPermission(Long userId, Long documentId, String requiredPermission) {
        // 先从缓存获取
        String cacheKey = DOC_PERM_CACHE_PREFIX + documentId + ":" + userId;
        Boolean cached = (Boolean) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return cached;
        }
        
        LocalDateTime now = LocalDateTime.now();
        
        // 1. 检查直接用户权限
        List<DocPermission> userPerms = docPermissionRepository.selectByDocumentIdAndUserId(documentId, userId);
        for (DocPermission perm : userPerms) {
            if (isValidPermission(perm, requiredPermission, now)) {
                redisTemplate.opsForValue().set(cacheKey, true, 10, TimeUnit.MINUTES);
                return true;
            }
        }
        
        // 2. 检查角色权限
        List<Long> roleIds = userRoleRepository.selectRoleIdsByUserId(userId);
        for (Long roleId : roleIds) {
            List<DocPermission> rolePerms = docPermissionRepository.selectByDocumentIdAndRoleId(documentId, roleId);
            for (DocPermission perm : rolePerms) {
                if (isValidPermission(perm, requiredPermission, now)) {
                    redisTemplate.opsForValue().set(cacheKey, true, 10, TimeUnit.MINUTES);
                    return true;
                }
            }
        }
        
        // 缓存否定结果
        redisTemplate.opsForValue().set(cacheKey, false, 10, TimeUnit.MINUTES);
        return false;
    }
    
    /**
     * 检查权限是否有效（未过期）
     */
    private boolean isValidPermission(DocPermission perm, String requiredPermission, LocalDateTime now) {
        // 检查过期时间
        if (perm.getExpiryDate() != null && perm.getExpiryDate().isBefore(now)) {
            return false;
        }
        
        // 检查权限级别
        String permType = perm.getPermissionType();
        if ("admin".equals(permType)) {
            return true;
        }
        if ("write".equals(permType)) {
            return "write".equals(requiredPermission) || "read".equals(requiredPermission);
        }
        if ("read".equals(permType)) {
            return "read".equals(requiredPermission);
        }
        return false;
    }
    
    /**
     * 清除用户权限缓存
     */
    public void clearCache(Long userId) {
        String cacheKey = PERMISSION_CACHE_PREFIX + userId;
        redisTemplate.delete(cacheKey);
    }
    
    /**
     * 清除文档权限缓存
     */
    public void clearDocPermissionCache(Long documentId) {
        // 清除所有用户的文档权限缓存
        // 实际生产中可能需要更精确的缓存管理
        redisTemplate.delete(DOC_PERM_CACHE_PREFIX + documentId + ":*");
    }
}