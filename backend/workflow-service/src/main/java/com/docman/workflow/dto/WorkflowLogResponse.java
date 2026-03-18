package com.docman.workflow.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 工作流日志响应
 */
@Data
public class WorkflowLogResponse {
    
    private Long id;
    private Long workflowId;
    private Long approverId;
    private String action;
    private String comment;
    private LocalDateTime createdAt;
}