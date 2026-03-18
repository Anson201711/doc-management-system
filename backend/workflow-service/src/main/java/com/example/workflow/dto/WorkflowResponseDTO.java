package com.example.workflow.dto;

import com.example.workflow.entity.ApprovalTask;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class WorkflowResponseDTO {
    private Long id;
    private String workflowType;
    private Long documentId;
    private Long initiatorId;
    private String status;
    private Integer currentLevel;
    private Integer totalLevels;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // 扩展字段
    private Long currentTaskId;
    private List<ApprovalTask> tasks;
}