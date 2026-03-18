package com.docman.permission.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.docman.permission.entity.RolePermission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 角色权限关联Repository
 */
@Mapper
public interface RolePermissionRepository extends BaseMapper<RolePermission> {
    
    /**
     * 根据角色ID查询权限ID列表
     */
    List<Long> selectPermissionIdsByRoleId(@Param("roleId") Long roleId);
}