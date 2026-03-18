package com.docman.workflow.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.docman.workflow.dto.*;
import com.docman.workflow.entity.Workflow;
import com.docman.workflow.entity.WorkflowLog;
import com.docman.workflow.mapper.WorkflowMapper;
import com.docman.workflow.mapper.WorkflowLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 工作流服务
 */
@Service
@RequiredArgsConstructor
public class WorkflowService extends ServiceImpl<WorkflowMapper, Workflow> {
    
    private final WorkflowLogMapper workflowLogMapper;
    private final NotificationService notificationService;
    
    /**
     * 创建审批流程
     */
    @Transactional(rollbackFor = Exception.class)
    public WorkflowResponse createWorkflow(CreateWorkflowRequest request, Long creatorId) {
        // 创建工作流
        Workflow workflow = new Workflow();
        workflow.setDocumentId(request.getDocumentId());
        workflow.setCreatorId(creatorId);
        workflow.setApproverId(request.getApproverId());
        workflow.setTitle(request.getTitle());
        workflow.setDescription(request.getDescription());
        workflow.setCurrentStatus("pending");
        workflow.setCreatedAt(LocalDateTime.now());
        
        this.save(workflow);
        
        // 发送审批通知
        notificationService.sendApprovalNotification(workflow);
        
        return toResponse(workflow);
    }
    
    /**
     * 获取工作流详情
     */
    public WorkflowResponse getWorkflowById(Long id) {
        Workflow workflow = this.getById(id);
        if (workflow == null) {
            return null;
        }
        return toResponse(workflow);
    }
    
    /**
     * 审批通过
     */
    @Transactional(rollbackFor = Exception.class)
    public WorkflowResponse approve(Long workflowId, Long approverId, String comment) {
        Workflow workflow = this.getById(workflowId);
        if (workflow == null) {
            throw new RuntimeException("工作流不存在");
        }
        
        if (!"pending".equals(workflow.getCurrentStatus())) {
            throw new RuntimeException("工作流状态不允许审批");
        }
        
        if (!approverId.equals(workflow.getApproverId())) {
            throw new RuntimeException("无审批权限");
        }
        
        // 更新工作流状态
        workflow.setCurrentStatus("approved");
        workflow.setCompletedAt(LocalDateTime.now());
        this.updateById(workflow);
        
        // 记录审批日志
        WorkflowLog log = new WorkflowLog();
        log.setWorkflowId(workflowId);
        log.setApproverId(approverId);
        log.setAction("approve");
        log.setComment(comment);
        log.setCreatedAt(LocalDateTime.now());
        workflowLogMapper.insert(log);
        
        // 发送通知
        notificationService.sendApprovalResultNotification(workflow, "approved");
        
        return toResponse(workflow);
    }
    
    /**
     * 审批拒绝
     */
    @Transactional(rollbackFor = Exception.class)
    public WorkflowResponse reject(Long workflowId, Long approverId, String comment) {
        Workflow workflow = this.getById(workflowId);
        if (workflow == null) {
            throw new RuntimeException("工作流不存在");
        }
        
        if (!"pending".equals(workflow.getCurrentStatus())) {
            throw new RuntimeException("工作流状态不允许审批");
        }
        
        if (!approverId.equals(workflow.getApproverId())) {
            throw new RuntimeException("无审批权限");
        }
        
        // 更新工作流状态
        workflow.setCurrentStatus("rejected");
        workflow.setCompletedAt(LocalDateTime.now());
        this.updateById(workflow);
        
        // 记录审批日志
        WorkflowLog log = new WorkflowLog();
        log.setWorkflowId(workflowId);
        log.setApproverId(approverId);
        log.setAction("reject");
        log.setComment(comment);
        log.setCreatedAt(LocalDateTime.now());
        workflowLogMapper.insert(log);
        
        // 发送通知
        notificationService.sendApprovalResultNotification(workflow, "rejected");
        
        return toResponse(workflow);
    }
    
    /**
     * 获取用户的待办工作流
     */
    public List<WorkflowResponse> getPendingWorkflows(Long approverId) {
        List<Workflow> workflows = baseMapper.findPendingByApproverId(approverId);
        return workflows.stream().map(this::toResponse).collect(Collectors.toList());
    }
    
    /**
     * 获取用户创建的工作流
     */
    public List<WorkflowResponse> getMyWorkflows(Long creatorId) {
        List<Workflow> workflows = baseMapper.findByCreatorId(creatorId);
        return workflows.stream().map(this::toResponse).collect(Collectors.toList());
    }
    
    /**
     * 获取工作流的审批记录
     */
    public List<WorkflowLogResponse> getWorkflowLogs(Long workflowId) {
        List<WorkflowLog> logs = workflowLogMapper.findByWorkflowId(workflowId);
        return logs.stream().map(this::toLogResponse).collect(Collectors.toList());
    }
    
    /**
     * 转换实体为响应对象
     */
    private WorkflowResponse toResponse(Workflow workflow) {
        WorkflowResponse response = new WorkflowResponse();
        response.setId(workflow.getId());
        response.setDocumentId(workflow.getDocumentId());
        response.setCreatorId(workflow.getCreatorId());
        response.setApproverId(workflow.getApproverId());
        response.setTitle(workflow.getTitle());
        response.setDescription(workflow.getDescription());
        response.setCurrentStatus(workflow.getCurrentStatus());
        response.setCreatedAt(workflow.getCreatedAt());
        response.setCompletedAt(workflow.getCompletedAt());
        return response;
    }
    
    /**
     * 转换日志实体为响应对象
     */
    private WorkflowLogResponse toLogResponse(WorkflowLog log) {
        WorkflowLogResponse response = new WorkflowLogResponse();
        response.setId(log.getId());
        response.setWorkflowId(log.getWorkflowId());
        response.setApproverId(log.getApproverId());
        response.setAction(log.getAction());
        response.setComment(log.getComment());
        response.setCreatedAt(log.getCreatedAt());
        return response;
    }
}