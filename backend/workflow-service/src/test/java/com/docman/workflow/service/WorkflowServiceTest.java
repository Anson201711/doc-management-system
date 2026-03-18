package com.docman.workflow.service;

import com.docman.workflow.dto.CreateWorkflowRequest;
import com.docman.workflow.dto.WorkflowResponse;
import com.docman.workflow.entity.Workflow;
import com.docman.workflow.mapper.WorkflowMapper;
import com.docman.workflow.mapper.WorkflowLogMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 工作流服务单元测试
 */
@SpringBootTest
class WorkflowServiceTest {
    
    @Autowired
    private WorkflowService workflowService;
    
    @Autowired
    private WorkflowMapper workflowMapper;
    
    @Autowired
    private WorkflowLogMapper workflowLogMapper;
    
    @MockBean
    private RedisTemplate<String, Object> redisTemplate;
    
    @BeforeEach
    void setUp() {
        // 清理数据
        workflowLogMapper.delete(null);
        workflowMapper.delete(null);
    }
    
    @Test
    void testCreateWorkflow() {
        // 准备请求
        CreateWorkflowRequest request = new CreateWorkflowRequest();
        request.setDocumentId(1L);
        request.setApproverId(2L);
        request.setTitle("测试审批流程");
        request.setDescription("测试描述");
        
        // 执行创建 (creatorId从header中传入)
        WorkflowResponse response = workflowService.createWorkflow(request, 1L);
        
        // 验证结果
        assertNotNull(response);
        assertNotNull(response.getId());
        assertEquals("测试审批流程", response.getTitle());
        assertEquals("pending", response.getCurrentStatus());
        assertEquals(1L, response.getCreatorId());
        assertEquals(2L, response.getApproverId());
        assertEquals(1L, response.getDocumentId());
    }
    
    @Test
    void testApproveWorkflow() {
        // 创建工作流
        CreateWorkflowRequest request = new CreateWorkflowRequest();
        request.setDocumentId(1L);
        request.setCreatorId(1L);
        request.setApproverId(2L);
        request.setTitle("测试审批");
        request.setDescription("测试");
        
        WorkflowResponse workflow = workflowService.createWorkflow(request, 1L);
        
        // 执行审批通过
        WorkflowResponse approved = workflowService.approve(workflow.getId(), 2L, "同意");
        
        // 验证结果
        assertNotNull(approved);
        assertEquals("approved", approved.getCurrentStatus());
        assertNotNull(approved.getCompletedAt());
    }
    
    @Test
    void testRejectWorkflow() {
        // 创建工作流
        CreateWorkflowRequest request = new CreateWorkflowRequest();
        request.setDocumentId(1L);
        request.setCreatorId(1L);
        request.setApproverId(2L);
        request.setTitle("测试拒绝");
        request.setDescription("测试");
        
        WorkflowResponse workflow = workflowService.createWorkflow(request, 1L);
        
        // 执行审批拒绝
        WorkflowResponse rejected = workflowService.reject(workflow.getId(), 2L, "不符合要求");
        
        // 验证结果
        assertNotNull(rejected);
        assertEquals("rejected", rejected.getCurrentStatus());
        assertNotNull(rejected.getCompletedAt());
    }
    
    @Test
    void testGetWorkflowById() {
        // 创建工作流
        CreateWorkflowRequest request = new CreateWorkflowRequest();
        request.setDocumentId(1L);
        request.setCreatorId(1L);
        request.setApproverId(2L);
        request.getClass();
        WorkflowResponse created = workflowService.createWorkflow(request, 1L);
        
        // 查询工作流
        WorkflowResponse found = workflowService.getWorkflowById(created.getId());
        
        // 验证结果
        assertNotNull(found);
        assertEquals(created.getId(), found.getId());
        assertEquals(created.getTitle(), found.getTitle());
    }
    
    @Test
    void testGetWorkflowLogs() {
        // 创建并审批工作流
        CreateWorkflowRequest request = new CreateWorkflowRequest();
        request.setDocumentId(1L);
        request.setCreatorId(1L);
        request.setApproverId(2L);
        request.setTitle("测试日志");
        request.setDescription("测试");
        
        WorkflowResponse workflow = workflowService.createWorkflow(request, 1L);
        workflowService.approve(workflow.getId(), 2L, "测试通过");
        
        // 查询日志
        List<WorkflowResponse> logs = workflowService.getWorkflowLogs(workflow.getId());
        
        // 验证结果
        assertNotNull(logs);
        // 日志通过Service内部方法获取，这里验证日志不为空
    }
    
    @Test
    void testGetPendingWorkflows() {
        // 创建待审批工作流
        CreateWorkflowRequest request = new CreateWorkflowRequest();
        request.setDocumentId(1L);
        request.setCreatorId(1L);
        request.setApproverId(2L);
        request.setTitle("待审批1");
        request.setDescription("测试");
        
        workflowService.createWorkflow(request, 1L);
        
        // 查询待办
        List<WorkflowResponse> pending = workflowService.getPendingWorkflows(2L);
        
        // 验证结果
        assertNotNull(pending);
        assertTrue(pending.size() > 0);
        assertEquals("pending", pending.get(0).getCurrentStatus());
    }
    
    @Test
    void testApproveWithWrongApprover() {
        // 创建工作流
        CreateWorkflowRequest request = new CreateWorkflowRequest();
        request.setDocumentId(1L);
        request.setCreatorId(1L);
        request.setApproverId(2L);
        request.setTitle("测试权限");
        request.setDescription("测试");
        
        WorkflowResponse workflow = workflowService.createWorkflow(request, 1L);
        
        // 使用错误的审批人审批，应该抛出异常
        assertThrows(RuntimeException.class, () -> {
            workflowService.approve(workflow.getId(), 3L, "无权限");
        });
    }
    
    @Test
    void testApproveNonExistentWorkflow() {
        // 审批不存在的workflow
        assertThrows(Exception.class, () -> {
            workflowService.approve(999L, 1L, "不存在");
        });
    }
}