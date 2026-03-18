package com.docman.user.service;

import com.docman.user.entity.Department;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 部门服务单元测试
 */
@SpringBootTest
@ActiveProfiles("test")
class DepartmentServiceTest {

    @Autowired
    private DepartmentService departmentService;

    @Test
    void testFindAllActive() {
        List<Department> departments = departmentService.findAllActive();
        assertNotNull(departments);
        assertFalse(departments.isEmpty());
    }

    @Test
    void testFindById() {
        Department department = departmentService.findById(1L);
        assertNotNull(department);
        assertEquals("技术部", department.getName());
    }

    @Test
    void testFindRootDepartments() {
        List<Department> departments = departmentService.findRootDepartments();
        assertNotNull(departments);
        assertTrue(departments.size() >= 3);
        // 验证顶级部门没有父ID
        for (Department dept : departments) {
            assertNull(dept.getParentId());
        }
    }

    @Test
    void testFindByParentId() {
        List<Department> children = departmentService.findByParentId(1L);
        assertNotNull(children);
        assertFalse(children.isEmpty());
        // 验证子部门的父ID
        for (Department child : children) {
            assertEquals(1L, child.getParentId());
        }
    }
}