package com.example.workflow.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("t_workflow")
public class Workflow {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String workflowType;
    
    private Long documentId;
    
    private Long initiatorId;
    
    private String status; // PENDING, APPROVING, APPROVED, REJECTED
    
    private Integer currentLevel;
    
    private Integer totalLevels;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
}