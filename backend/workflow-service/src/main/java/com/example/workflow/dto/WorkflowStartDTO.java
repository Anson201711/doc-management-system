package com.example.workflow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class WorkflowStartDTO {
    
    @NotBlank(message = "工作流类型不能为空")
    private String workflowType;
    
    @NotNull(message = "文档ID不能为空")
    private Long documentId;
    
    @NotNull(message = "发起人ID不能为空")
    private Long initiatorId;
    
    private List<Long> approverIds; // 审批人列表，支持多级审批
    
    private Integer totalLevels; // 审批级别数，默认为1
}