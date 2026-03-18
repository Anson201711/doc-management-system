package com.docman.permission.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.docman.permission.dto.DocPermissionDTO;
import com.docman.permission.entity.DocPermission;
import com.docman.permission.repository.DocPermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 文档权限服务 - 文档级别的细粒度权限控制
 */
@Service
@RequiredArgsConstructor
public class DocPermissionService {
    
    private final DocPermissionRepository docPermissionRepository;
    
    /**
     * 根据文档ID查询权限列表
     */
    public List<DocPermission> findByDocumentId(Long documentId) {
        return docPermissionRepository.selectByDocumentId(documentId);
    }
    
    /**
     * 根据文档ID和用户ID查询权限
     */
    public List<DocPermission> findByDocumentIdAndUserId(Long documentId, Long userId) {
        return docPermissionRepository.selectByDocumentIdAndUserId(documentId, userId);
    }
    
    /**
     * 为文档添加用户权限
     */
    public DocPermission addUserPermission(DocPermissionDTO dto) {
        // 检查是否已存在
        LambdaQueryWrapper<DocPermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DocPermission::getDocumentId, dto.getDocumentId())
               .eq(DocPermission::getUserId, dto.getUserId());
        DocPermission existing = docPermissionRepository.selectOne(wrapper);
        
        if (existing != null) {
            // 更新权限
            existing.setPermissionType(dto.getPermissionType());
            existing.setExpiryDate(dto.getExpiryDate());
            existing.setUpdatedAt(LocalDateTime.now());
            docPermissionRepository.updateById(existing);
            return existing;
        }
        
        DocPermission permission = new DocPermission();
        permission.setDocumentId(dto.getDocumentId());
        permission.setUserId(dto.getUserId());
        permission.setPermissionType(dto.getPermissionType());
        permission.setExpiryDate(dto.getExpiryDate());
        permission.setCreatedBy(dto.getUserId());
        permission.setCreatedAt(LocalDateTime.now());
        permission.setUpdatedAt(LocalDateTime.now());
        docPermissionRepository.insert(permission);
        return permission;
    }
    
    /**
     * 为文档添加角色权限
     */
    public DocPermission addRolePermission(DocPermissionDTO dto) {
        // 检查是否已存在
        LambdaQueryWrapper<DocPermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DocPermission::getDocumentId, dto.getDocumentId())
               .eq(DocPermission::getRoleId, dto.getRoleId());
        DocPermission existing = docPermissionRepository.selectOne(wrapper);
        
        if (existing != null) {
            existing.setPermissionType(dto.getPermissionType());
            existing.setExpiryDate(dto.getExpiryDate());
            existing.setUpdatedAt(LocalDateTime.now());
            docPermissionRepository.updateById(existing);
            return existing;
        }
        
        DocPermission permission = new DocPermission();
        permission.setDocumentId(dto.getDocumentId());
        permission.setRoleId(dto.getRoleId());
        permission.setPermissionType(dto.getPermissionType());
        permission.setExpiryDate(dto.getExpiryDate());
        permission.setCreatedBy(dto.getUserId());
        permission.setCreatedAt(LocalDateTime.now());
        permission.setUpdatedAt(LocalDateTime.now());
        docPermissionRepository.insert(permission);
        return permission;
    }
    
    /**
     * 批量添加用户权限
     */
    @Transactional
    public List<DocPermission> batchAddUserPermissions(DocPermissionDTO dto) {
        List<DocPermission> results = new ArrayList<>();
        if (dto.getUserIds() != null) {
            for (Long userId : dto.getUserIds()) {
                DocPermissionDTO singleDto = new DocPermissionDTO();
                singleDto.setDocumentId(dto.getDocumentId());
                singleDto.setUserId(userId);
                singleDto.setPermissionType(dto.getPermissionType());
                singleDto.setExpiryDate(dto.getExpiryDate());
                results.add(addUserPermission(singleDto));
            }
        }
        return results;
    }
    
    /**
     * 批量添加角色权限
     */
    @Transactional
    public List<DocPermission> batchAddRolePermissions(DocPermissionDTO dto) {
        List<DocPermission> results = new ArrayList<>();
        if (dto.getRoleIds() != null) {
            for (Long roleId : dto.getRoleIds()) {
                DocPermissionDTO singleDto = new DocPermissionDTO();
                singleDto.setDocumentId(dto.getDocumentId());
                singleDto.setRoleId(roleId);
                singleDto.setPermissionType(dto.getPermissionType());
                singleDto.setExpiryDate(dto.getExpiryDate());
                results.add(addRolePermission(singleDto));
            }
        }
        return results;
    }
    
    /**
     * 更新文档权限
     */
    public DocPermission update(Long id, DocPermissionDTO dto) {
        DocPermission permission = docPermissionRepository.selectById(id);
        if (permission == null) {
            throw new RuntimeException("文档权限不存在");
        }
        if (dto.getPermissionType() != null) {
            permission.setPermissionType(dto.getPermissionType());
        }
        if (dto.getExpiryDate() != null) {
            permission.setExpiryDate(dto.getExpiryDate());
        }
        permission.setUpdatedAt(LocalDateTime.now());
        docPermissionRepository.updateById(permission);
        return permission;
    }
    
    /**
     * 删除文档权限
     */
    public void delete(Long id) {
        docPermissionRepository.deleteById(id);
    }
    
    /**
     * 删除文档的所有权限
     */
    @Transactional
    public void deleteByDocumentId(Long documentId) {
        LambdaQueryWrapper<DocPermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DocPermission::getDocumentId, documentId);
        docPermissionRepository.delete(wrapper);
    }
    
    /**
     * 检查用户是否具有指定权限
     */
    public boolean hasPermission(Long documentId, Long userId, String requiredPermission) {
        // 检查是否有过期
        LocalDateTime now = LocalDateTime.now();
        
        LambdaQueryWrapper<DocPermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DocPermission::getDocumentId, documentId)
               .and(w -> w.eq(DocPermission::getUserId, userId).or().isNull(DocPermission::getUserId))
               .and(w -> w.isNull(DocPermission::getExpiryDate).or().ge(DocPermission::getExpiryDate, now));
        
        List<DocPermission> permissions = docPermissionRepository.selectList(wrapper);
        
        for (DocPermission permission : permissions) {
            if (hasPermissionLevel(permission.getPermissionType(), requiredPermission)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 检查权限级别
     */
    private boolean hasPermissionLevel(String actualPermission, String requiredPermission) {
        // admin > write > read
        if ("admin".equals(actualPermission)) {
            return true;
        }
        if ("write".equals(actualPermission)) {
            return "write".equals(requiredPermission) || "read".equals(requiredPermission);
        }
        if ("read".equals(actualPermission)) {
            return "read".equals(requiredPermission);
        }
        return false;
    }
}