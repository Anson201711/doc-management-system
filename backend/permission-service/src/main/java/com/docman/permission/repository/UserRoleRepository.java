package com.docman.permission.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.docman.permission.entity.UserRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 用户角色关联Repository
 */
@Mapper
public interface UserRoleRepository extends BaseMapper<UserRole> {
    
    /**
     * 根据用户ID查询角色ID列表
     */
    List<Long> selectRoleIdsByUserId(@Param("userId") Long userId);
}