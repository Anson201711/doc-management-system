package com.docman.workflow.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 工作流实体
 */
@Data
@TableName("workflows")
public class Workflow {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 文档ID
     */
    private Long documentId;
    
    /**
     * 创建人ID
     */
    private Long creatorId;
    
    /**
     * 审批人ID
     */
    private Long approverId;
    
    /**
     * 工作流标题
     */
    private String title;
    
    /**
     * 工作流描述
     */
    private String description;
    
    /**
     * 当前状态: pending(待审批), approved(已通过), rejected(已拒绝)
     */
    private String currentStatus;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    /**
     * 完成时间
     */
    private LocalDateTime completedAt;
    
    /**
     * 逻辑删除标记
     */
    @TableLogic
    private Integer deleted;
}