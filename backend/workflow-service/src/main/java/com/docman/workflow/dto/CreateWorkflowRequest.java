package com.docman.workflow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 创建工作流请求
 */
@Data
public class CreateWorkflowRequest {
    
    @NotNull(message = "文档ID不能为空")
    private Long documentId;
    
    @NotBlank(message = "标题不能为空")
    private String title;
    
    private String description;
    
    @NotNull(message = "审批人ID不能为空")
    private Long approverId;
}