package com.docman.document.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文档版本实体
 */
@Data
@TableName("doc_versions")
public class DocumentVersion {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 文档ID
     */
    private Long documentId;
    
    /**
     * 版本号
     */
    private Integer version;
    
    /**
     * 版本内容
     */
    private String content;
    
    /**
     * 变更摘要
     */
    private String changeSummary;
    
    /**
     * 创建者ID
     */
    private Long creatorId;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}