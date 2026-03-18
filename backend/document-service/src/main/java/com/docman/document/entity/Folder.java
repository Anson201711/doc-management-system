package com.docman.document.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文件夹实体
 */
@Data
@TableName("folders")
public class Folder {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 文件夹名称
     */
    private String name;
    
    /**
     * 父文件夹ID
     */
    private Long parentId;
    
    /**
     * 所有者ID
     */
    private Long ownerId;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}