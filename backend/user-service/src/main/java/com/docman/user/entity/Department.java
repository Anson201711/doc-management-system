package com.docman.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 部门实体
 */
@Data
@TableName("departments")
public class Department {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String name;
    
    private Long parentId;
    
    private String code;
    
    private String description;
    
    private Integer sortOrder;
    
    private String status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    
    @TableLogic
    private Integer deleted;
}