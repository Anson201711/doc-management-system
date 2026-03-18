package com.docman.permission.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 权限实体
 */
@Data
@TableName("permissions")
public class Permission {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String name;
    
    private String resource;
    
    private String action;
    
    private String description;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}