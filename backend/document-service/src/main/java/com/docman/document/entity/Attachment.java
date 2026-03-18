package com.docman.document.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 附件实体
 */
@Data
@TableName("attachments")
public class Attachment {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 文档ID
     */
    private Long documentId;
    
    /**
     * 文件名
     */
    private String fileName;
    
    /**
     * 文件大小
     */
    private Long fileSize;
    
    /**
     * 文件类型
     */
    private String fileType;
    
    /**
     * 存储Key (MinIO对象Key)
     */
    private String storageKey;
    
    /**
     * 上传者ID
     */
    private Long uploaderId;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}