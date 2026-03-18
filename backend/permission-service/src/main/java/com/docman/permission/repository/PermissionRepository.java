package com.docman.permission.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.docman.permission.entity.Permission;
import org.apache.ibatis.annotations.Mapper;

/**
 * 权限Repository
 */
@Mapper
public interface PermissionRepository extends BaseMapper<Permission> {
}