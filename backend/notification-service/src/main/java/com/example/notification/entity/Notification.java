package com.example.notification.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("notifications")
public class Notification {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private String type;
    
    private String title;
    
    private String content;
    
    private String sender;
    
    @TableField("is_read")
    private Boolean read;
    
    private String link;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}