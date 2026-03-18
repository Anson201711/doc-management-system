package com.docman.document.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文档实体
 */
@Data
@TableName("documents")
public class Document {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 文档标题
     */
    private String title;
    
    /**
     * 文档内容
     */
    private String content;
    
    /**
     * 文件夹ID
     */
    private Long folderId;
    
    /**
     * 创建者ID
     */
    private Long creatorId;
    
    /**
     * 文档状态: draft, published, archived
     */
    private String status;
    
    /**
     * 文档类型
     */
    private String documentType;
    
    /**
     * 标签 (JSON数组)
     */
    private String tags;
    
    /**
     * 当前版本号
     */
    private Integer currentVersion;
    
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