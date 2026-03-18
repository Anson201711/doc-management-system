package com.docman.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户实体
 */
@Data
@TableName("users")
public class User {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String username;
    
    private String email;
    
    private String passwordHash;
    
    private String fullName;
    
    private String avatarUrl;
    
    private String phone;
    
    private Long departmentId;
    
    /**
     * 用户状态: active, inactive, locked
     */
    private String status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    
    /**
     * 逻辑删除标志
     */
    @TableLogic
    private Integer deleted;
}