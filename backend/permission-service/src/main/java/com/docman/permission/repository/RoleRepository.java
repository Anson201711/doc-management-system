package com.docman.permission.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.docman.permission.entity.Role;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色Repository
 */
@Mapper
public interface RoleRepository extends BaseMapper<Role> {
}