package com.docman.permission.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 文档权限实体 - 文档级别的细粒度权限控制
 */
@Data
@TableName("doc_permissions")
public class DocPermission {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long documentId;
    
    private Long userId;
    
    private Long roleId;
    
    private String permissionType;  // read, write, admin
    
    private LocalDateTime expiryDate;
    
    private Long createdBy;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}