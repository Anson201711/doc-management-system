package com.docman.workflow.controller;

import com.docman.workflow.common.Result;
import com.docman.workflow.dto.*;
import com.docman.workflow.service.WorkflowService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 工作流控制器
 */
@RestController
@RequestMapping("/api/v1/workflows")
@RequiredArgsConstructor
public class WorkflowController {
    
    private final WorkflowService workflowService;
    
    /**
     * 创建审批流程
     */
    @PostMapping
    public Result<WorkflowResponse> createWorkflow(
            @Valid @RequestBody CreateWorkflowRequest request,
            @RequestHeader("X-User-Id") Long userId) {
        WorkflowResponse response = workflowService.createWorkflow(request, userId);
        return Result.success(response);
    }
    
    /**
     * 获取工作流详情
     */
    @GetMapping("/{id}")
    public Result<WorkflowResponse> getWorkflow(@PathVariable Long id) {
        WorkflowResponse response = workflowService.getWorkflowById(id);
        if (response == null) {
            return Result.error(404, "工作流不存在");
        }
        return Result.success(response);
    }
    
    /**
     * 审批通过
     */
    @PostMapping("/{id}/approve")
    public Result<WorkflowResponse> approve(
            @PathVariable Long id,
            @RequestBody ApproveRequest request,
            @RequestHeader("X-User-Id") Long userId) {
        try {
            WorkflowResponse response = workflowService.approve(id, userId, request.getComment());
            return Result.success(response);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 审批拒绝
     */
    @PostMapping("/{id}/reject")
    public Result<WorkflowResponse> reject(
            @PathVariable Long id,
            @RequestBody ApproveRequest request,
            @RequestHeader("X-User-Id") Long userId) {
        try {
            WorkflowResponse response = workflowService.reject(id, userId, request.getComment());
            return Result.success(response);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取我的待办流程
     */
    @GetMapping("/my/tasks")
    public Result<List<WorkflowResponse>> getMyTasks(@RequestHeader("X-User-Id") Long userId) {
        List<WorkflowResponse> workflows = workflowService.getPendingWorkflows(userId);
        return Result.success(workflows);
    }
    
    /**
     * 获取我创建的流程
     */
    @GetMapping("/my")
    public Result<List<WorkflowResponse>> getMyWorkflows(@RequestHeader("X-User-Id") Long userId) {
        List<WorkflowResponse> workflows = workflowService.getMyWorkflows(userId);
        return Result.success(workflows);
    }
    
    /**
     * 获取审批记录
     */
    @GetMapping("/{id}/logs")
    public Result<List<WorkflowLogResponse>> getWorkflowLogs(@PathVariable Long id) {
        List<WorkflowLogResponse> logs = workflowService.getWorkflowLogs(id);
        return Result.success(logs);
    }
}