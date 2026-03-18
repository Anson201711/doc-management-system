package com.example.workflow.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.workflow.dto.ApprovalDTO;
import com.example.workflow.dto.WorkflowResponseDTO;
import com.example.workflow.dto.WorkflowStartDTO;
import com.example.workflow.service.WorkflowService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/workflows")
@RequiredArgsConstructor
public class WorkflowController {

    private final WorkflowService workflowService;

    /**
     * 创建审批流程
     * POST /api/v1/workflows
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createWorkflow(
            @Valid @RequestBody WorkflowStartDTO dto) {
        WorkflowResponseDTO workflow = workflowService.startWorkflow(dto);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "审批流程创建成功");
        response.put("data", workflow);
        return ResponseEntity.ok(response);
    }

    /**
     * 获取审批流程详情
     * GET /api/v1/workflows/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getWorkflow(@PathVariable Long id) {
        WorkflowResponseDTO workflow = workflowService.getWorkflow(id);
        Map<String, Object> response = new HashMap<>();
        if (workflow != null) {
            response.put("code", 200);
            response.put("data", workflow);
        } else {
            response.put("code", 404);
            response.put("message", "审批流程不存在");
        }
        return ResponseEntity.ok(response);
    }

    /**
     * 根据文档ID获取审批流程
     * GET /api/v1/workflows?docId=xxx
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getWorkflowsByDocumentId(
            @RequestParam(required = false) Long docId,
            @RequestParam(required = false) Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status) {
        
        Map<String, Object> response = new HashMap<>();
        
        if (docId != null) {
            // 获取文档的审批流程
            List<WorkflowResponseDTO> workflows = workflowService.getWorkflowsByDocumentId(docId);
            response.put("code", 200);
            response.put("data", workflows);
        } else if (userId != null) {
            // 获取用户待审批的任务
            List<WorkflowResponseDTO> workflows = workflowService.getMyApprovalTasks(userId);
            response.put("code", 200);
            response.put("data", workflows);
        } else {
            // 分页查询所有工作流
            IPage<WorkflowResponseDTO> pageResult = workflowService.getWorkflows(page, size, status);
            response.put("code", 200);
            response.put("data", pageResult);
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * 我的审批任务
     * GET /api/v1/workflows/my
     */
    @GetMapping("/my")
    public ResponseEntity<Map<String, Object>> getMyApprovalTasks(
            @RequestParam Long userId) {
        List<WorkflowResponseDTO> workflows = workflowService.getMyApprovalTasks(userId);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", workflows);
        return ResponseEntity.ok(response);
    }

    /**
     * 审批通过
     * PUT /api/v1/workflows/{id}/approve
     */
    @PutMapping("/{id}/approve")
    public ResponseEntity<Map<String, Object>> approve(
            @PathVariable Long id,
            @RequestBody ApprovalDTO dto) {
        try {
            WorkflowResponseDTO workflow = workflowService.approve(id, dto.getTaskId(), dto.getApproverId(), dto.getComment());
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "审批通过");
            response.put("data", workflow);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 400);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 审批拒绝
     * PUT /api/v1/workflows/{id}/reject
     */
    @PutMapping("/{id}/reject")
    public ResponseEntity<Map<String, Object>> reject(
            @PathVariable Long id,
            @RequestBody ApprovalDTO dto) {
        try {
            WorkflowResponseDTO workflow = workflowService.reject(id, dto.getTaskId(), dto.getApproverId(), dto.getComment());
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "审批已拒绝");
            response.put("data", workflow);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 400);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}