package com.docman.workflow.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 工作流响应
 */
@Data
public class WorkflowResponse {
    
    private Long id;
    private Long documentId;
    private Long creatorId;
    private Long approverId;
    private String title;
    private String description;
    private String currentStatus;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
}