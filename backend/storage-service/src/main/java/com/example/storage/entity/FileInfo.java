package com.example.storage.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * File Info Entity
 * Represents stored file metadata
 */
@Data
@TableName("file_info")
public class FileInfo {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * File name
     */
    private String fileName;
    
    /**
     * Original file name
     */
    private String originalName;
    
    /**
     * File size in bytes
     */
    private Long fileSize;
    
    /**
     * MIME type
     */
    private String contentType;
    
    /**
     * File extension
     */
    private String extension;
    
    /**
     * MinIO object name (path in bucket)
     */
    private String objectName;
    
    /**
     * File bucket name
     */
    private String bucketName;
    
    /**
     * File URL
     */
    private String fileUrl;
    
    /**
     * File hash (MD5)
     */
    private String fileHash;
    
    /**
     * Upload user ID
     */
    private Long uploadUserId;
    
    /**
     * Upload username
     */
    private String uploadUserName;
    
    /**
     * Related document ID
     */
    private Long documentId;
    
    /**
     * File status (active, deleted)
     */
    private String status;
    
    /**
     * Created timestamp
     */
    private LocalDateTime createdAt;
    
    /**
     * Updated timestamp
     */
    private LocalDateTime updatedAt;
}