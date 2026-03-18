package com.docman.permission.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 角色权限关联实体
 */
@Data
@TableName("role_permissions")
public class RolePermission {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long roleId;
    
    private Long permissionId;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}