package com.example.workflow.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.workflow.dto.WorkflowStartDTO;
import com.example.workflow.dto.WorkflowResponseDTO;
import com.example.workflow.entity.ApprovalTask;
import com.example.workflow.entity.Workflow;
import com.example.workflow.repository.ApprovalTaskRepository;
import com.example.workflow.repository.WorkflowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkflowService {

    private final WorkflowRepository workflowRepository;
    private final ApprovalTaskRepository approvalTaskRepository;

    // 工作流状态常量
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_APPROVING = "APPROVING";
    public static final String STATUS_APPROVED = "APPROVED";
    public static final String STATUS_REJECTED = "REJECTED";

    // 审批任务状态常量
    public static final String TASK_STATUS_PENDING = "PENDING";
    public static final String TASK_STATUS_APPROVED = "APPROVED";
    public static final String TASK_STATUS_REJECTED = "REJECTED";

    /**
     * 创建审批流程
     */
    @Transactional
    public WorkflowResponseDTO startWorkflow(WorkflowStartDTO dto) {
        // 1. 创建工作流记录
        Workflow workflow = new Workflow();
        workflow.setWorkflowType(dto.getWorkflowType());
        workflow.setDocumentId(dto.getDocumentId());
        workflow.setInitiatorId(dto.getInitiatorId());
        workflow.setStatus(STATUS_APPROVING);
        workflow.setCurrentLevel(1);
        workflow.setTotalLevels(dto.getTotalLevels() != null ? dto.getTotalLevels() : 1);
        workflow.setCreateTime(LocalDateTime.now());
        workflow.setUpdateTime(LocalDateTime.now());
        workflow.setDeleted(0);

        workflowRepository.insert(workflow);

        // 2. 为每个审批级别创建审批任务
        List<Long> approverIds = dto.getApproverIds();
        if (approverIds == null || approverIds.isEmpty()) {
            approverIds = new ArrayList<>();
            approverIds.add(dto.getInitiatorId()); // 默认自己审批
        }

        int totalLevels = workflow.getTotalLevels();
        for (int level = 1; level <= totalLevels; level++) {
            // 如果只有一个级别，所有审批人都参与该级别
            // 如果有多个级别，按顺序分配审批人
            Long approverId = approverIds.get(Math.min(level - 1, approverIds.size() - 1));

            ApprovalTask task = new ApprovalTask();
            task.setWorkflowId(workflow.getId());
            task.setApproverId(approverId);
            task.setLevel(level);
            task.setStatus(level == 1 ? TASK_STATUS_PENDING : TASK_STATUS_PENDING);
            task.setCreateTime(LocalDateTime.now());
            task.setUpdateTime(LocalDateTime.now());
            task.setDeleted(0);

            approvalTaskRepository.insert(task);
        }

        return convertToDTO(workflow);
    }

    /**
     * 获取审批流程详情
     */
    public WorkflowResponseDTO getWorkflow(Long id) {
        Workflow workflow = workflowRepository.selectById(id);
        if (workflow == null) {
            return null;
        }
        return convertToDTO(workflow);
    }

    /**
     * 根据文档ID获取审批流程
     */
    public List<WorkflowResponseDTO> getWorkflowsByDocumentId(Long documentId) {
        LambdaQueryWrapper<Workflow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Workflow::getDocumentId, documentId)
               .orderByDesc(Workflow::getCreateTime);
        List<Workflow> workflows = workflowRepository.selectList(wrapper);
        return workflows.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    /**
     * 获取用户待审批的任务
     */
    public List<WorkflowResponseDTO> getMyApprovalTasks(Long userId) {
        // 查找当前用户待审批的任务
        LambdaQueryWrapper<ApprovalTask> taskWrapper = new LambdaQueryWrapper<>();
        taskWrapper.eq(ApprovalTask::getApproverId, userId)
                   .eq(ApprovalTask::getStatus, TASK_STATUS_PENDING);
        List<ApprovalTask> tasks = approvalTaskRepository.selectList(taskWrapper);

        // 获取对应的工作流
        List<WorkflowResponseDTO> result = new ArrayList<>();
        for (ApprovalTask task : tasks) {
            Workflow workflow = workflowRepository.selectById(task.getWorkflowId());
            if (workflow != null) {
                WorkflowResponseDTO dto = convertToDTO(workflow);
                dto.setCurrentTaskId(task.getId());
                dto.setCurrentLevel(task.getLevel());
                result.add(dto);
            }
        }
        return result;
    }

    /**
     * 审批通过
     */
    @Transactional
    public WorkflowResponseDTO approve(Long workflowId, Long taskId, Long approverId, String comment) {
        // 1. 查找审批任务
        ApprovalTask task = approvalTaskRepository.selectById(taskId);
        if (task == null) {
            throw new RuntimeException("审批任务不存在");
        }

        // 2. 验证审批人权限
        if (!task.getApproverId().equals(approverId)) {
            throw new RuntimeException("无权限审批此任务");
        }

        // 3. 更新任务状态
        task.setStatus(TASK_STATUS_APPROVED);
        task.setComment(comment);
        task.setUpdateTime(LocalDateTime.now());
        approvalTaskRepository.updateById(task);

        // 4. 查找工作流
        Workflow workflow = workflowRepository.selectById(workflowId);
        if (workflow == null) {
            throw new RuntimeException("工作流不存在");
        }

        // 5. 检查是否还有下一级审批
        int currentLevel = task.getLevel();
        if (currentLevel < workflow.getTotalLevels()) {
            // 激活下一级审批任务
            LambdaQueryWrapper<ApprovalTask> nextTaskWrapper = new LambdaQueryWrapper<>();
            nextTaskWrapper.eq(ApprovalTask::getWorkflowId, workflowId)
                          .eq(ApprovalTask::getLevel, currentLevel + 1);
            ApprovalTask nextTask = approvalTaskRepository.selectOne(nextTaskWrapper);
            if (nextTask != null) {
                nextTask.setStatus(TASK_STATUS_PENDING);
                nextTask.setUpdateTime(LocalDateTime.now());
                approvalTaskRepository.updateById(nextTask);
            }

            // 更新工作流当前级别
            workflow.setCurrentLevel(currentLevel + 1);
            workflow.setUpdateTime(LocalDateTime.now());
            workflowRepository.updateById(workflow);
        } else {
            // 所有审批级别完成，工作流审批通过
            workflow.setStatus(STATUS_APPROVED);
            workflow.setUpdateTime(LocalDateTime.now());
            workflowRepository.updateById(workflow);
        }

        return convertToDTO(workflow);
    }

    /**
     * 审批拒绝
     */
    @Transactional
    public WorkflowResponseDTO reject(Long workflowId, Long taskId, Long approverId, String comment) {
        // 1. 查找审批任务
        ApprovalTask task = approvalTaskRepository.selectById(taskId);
        if (task == null) {
            throw new RuntimeException("审批任务不存在");
        }

        // 2. 验证审批人权限
        if (!task.getApproverId().equals(approverId)) {
            throw new RuntimeException("无权限审批此任务");
        }

        // 3. 更新任务状态
        task.setStatus(TASK_STATUS_REJECTED);
        task.setComment(comment);
        task.setUpdateTime(LocalDateTime.now());
        approvalTaskRepository.updateById(task);

        // 4. 更新工作流状态为拒绝
        Workflow workflow = workflowRepository.selectById(workflowId);
        if (workflow != null) {
            workflow.setStatus(STATUS_REJECTED);
            workflow.setUpdateTime(LocalDateTime.now());
            workflowRepository.updateById(workflow);
        }

        return convertToDTO(workflow);
    }

    /**
     * 分页查询工作流
     */
    public IPage<WorkflowResponseDTO> getWorkflows(int page, int size, String status) {
        Page<Workflow> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Workflow> wrapper = new LambdaQueryWrapper<>();
        if (status != null && !status.isEmpty()) {
            wrapper.eq(Workflow::getStatus, status);
        }
        wrapper.orderByDesc(Workflow::getCreateTime);

        IPage<Workflow> workflowPage = workflowRepository.selectPage(pageParam, wrapper);

        return workflowPage.convert(this::convertToDTO);
    }

    /**
     * 转换为响应DTO
     */
    private WorkflowResponseDTO convertToDTO(Workflow workflow) {
        WorkflowResponseDTO dto = new WorkflowResponseDTO();
        dto.setId(workflow.getId());
        dto.setWorkflowType(workflow.getWorkflowType());
        dto.setDocumentId(workflow.getDocumentId());
        dto.setInitiatorId(workflow.getInitiatorId());
        dto.setStatus(workflow.getStatus());
        dto.setCurrentLevel(workflow.getCurrentLevel());
        dto.setTotalLevels(workflow.getTotalLevels());
        dto.setCreateTime(workflow.getCreateTime());
        dto.setUpdateTime(workflow.getUpdateTime());

        // 获取审批任务列表
        LambdaQueryWrapper<ApprovalTask> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ApprovalTask::getWorkflowId, workflow.getId())
               .orderByAsc(ApprovalTask::getLevel);
        List<ApprovalTask> tasks = approvalTaskRepository.selectList(wrapper);
        dto.setTasks(tasks);

        return dto;
    }
}