package com.docman.user.controller;

import com.docman.user.dto.ApiResponse;
import com.docman.user.entity.Department;
import com.docman.user.service.DepartmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 部门管理控制器
 */
@RestController
@RequestMapping("/api/v1/departments")
@RequiredArgsConstructor
@Tag(name = "部门管理", description = "部门查询接口")
public class DepartmentController {

    private final DepartmentService departmentService;

    @GetMapping
    @Operation(summary = "获取所有部门", description = "获取所有启用的部门列表")
    public ResponseEntity<ApiResponse<List<Department>>> getAllDepartments() {
        List<Department> departments = departmentService.findAllActive();
        return ResponseEntity.ok(ApiResponse.success(departments));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取部门详情", description = "根据ID获取部门详细信息")
    public ResponseEntity<ApiResponse<Department>> getDepartmentById(@PathVariable Long id) {
        Department department = departmentService.findById(id);
        if (department == null) {
            return ResponseEntity.badRequest().body(ApiResponse.error("部门不存在"));
        }
        return ResponseEntity.ok(ApiResponse.success(department));
    }

    @GetMapping("/root")
    @Operation(summary = "获取顶级部门", description = "获取所有顶级部门列表")
    public ResponseEntity<ApiResponse<List<Department>>> getRootDepartments() {
        List<Department> departments = departmentService.findRootDepartments();
        return ResponseEntity.ok(ApiResponse.success(departments));
    }

    @GetMapping("/{parentId}/children")
    @Operation(summary = "获取子部门", description = "根据父部门ID获取子部门列表")
    public ResponseEntity<ApiResponse<List<Department>>> getChildren(@PathVariable Long parentId) {
        List<Department> departments = departmentService.findByParentId(parentId);
        return ResponseEntity.ok(ApiResponse.success(departments));
    }
}