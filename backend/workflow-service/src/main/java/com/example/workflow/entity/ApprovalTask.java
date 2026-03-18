package com.example.workflow.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("t_approval_task")
public class ApprovalTask {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long workflowId;
    
    private Long approverId;
    
    private Integer level;
    
    private String status; // PENDING, APPROVED, REJECTED
    
    private String comment;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
}