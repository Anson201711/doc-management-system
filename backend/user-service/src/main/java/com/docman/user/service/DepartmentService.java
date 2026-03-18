package com.docman.user.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.docman.user.entity.Department;
import com.docman.user.mapper.DepartmentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 部门服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DepartmentService extends ServiceImpl<DepartmentMapper, Department> {

    /**
     * 获取所有启用的部门
     */
    public List<Department> findAllActive() {
        return baseMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Department>()
                .eq(Department::getStatus, "active")
                .orderByAsc(Department::getSortOrder)
        );
    }

    /**
     * 根据ID查询部门
     */
    public Department findById(Long id) {
        return baseMapper.selectById(id);
    }

    /**
     * 根据父部门ID查询子部门
     */
    public List<Department> findByParentId(Long parentId) {
        return baseMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Department>()
                .eq(Department::getParentId, parentId)
                .eq(Department::getStatus, "active")
                .orderByAsc(Department::getSortOrder)
        );
    }

    /**
     * 获取顶级部门列表
     */
    public List<Department> findRootDepartments() {
        return baseMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Department>()
                .isNull(Department::getParentId)
                .eq(Department::getStatus, "active")
                .orderByAsc(Department::getSortOrder)
        );
    }
}