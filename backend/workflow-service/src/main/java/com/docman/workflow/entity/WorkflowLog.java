package com.docman.workflow.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 工作流日志实体
 */
@Data
@TableName("workflow_logs")
public class WorkflowLog {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 工作流ID
     */
    private Long workflowId;
    
    /**
     * 审批人ID
     */
    private Long approverId;
    
    /**
     * 操作: approve(通过), reject(拒绝)
     */
    private String action;
    
    /**
     * 审批意见
     */
    private String comment;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    /**
     * 逻辑删除标记
     */
    @TableLogic
    private Integer deleted;
}