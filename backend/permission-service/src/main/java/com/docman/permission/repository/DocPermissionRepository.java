package com.docman.permission.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.docman.permission.entity.DocPermission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 文档权限Repository
 */
@Mapper
public interface DocPermissionRepository extends BaseMapper<DocPermission> {
    
    /**
     * 根据文档ID查询权限列表
     */
    default List<DocPermission> selectByDocumentId(@Param("docId") Long docId) {
        return this.selectList(new LambdaQueryWrapper<DocPermission>()
                .eq(DocPermission::getDocumentId, docId));
    }
    
    /**
     * 根据文档ID和用户ID查询权限
     */
    default List<DocPermission> selectByDocumentIdAndUserId(@Param("docId") Long docId, @Param("userId") Long userId) {
        return this.selectList(new LambdaQueryWrapper<DocPermission>()
                .eq(DocPermission::getDocumentId, docId)
                .eq(DocPermission::getUserId, userId));
    }
    
    /**
     * 根据文档ID和角色ID查询权限
     */
    default List<DocPermission> selectByDocumentIdAndRoleId(@Param("docId") Long docId, @Param("roleId") Long roleId) {
        return this.selectList(new LambdaQueryWrapper<DocPermission>()
                .eq(DocPermission::getDocumentId, docId)
                .eq(DocPermission::getRoleId, roleId));
    }
}