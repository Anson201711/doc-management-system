package com.example.search.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Search Document Entity
 * Represents indexed document for full-text search
 */
@Data
@TableName("search_documents")
public class SearchDocument {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * Document ID (reference to original document)
     */
    private Long documentId;
    
    /**
     * Document title
     */
    private String title;
    
    /**
     * Document content (full text)
     */
    private String content;
    
    /**
     * Document type
     */
    private String documentType;
    
    /**
     * File extension
     */
    private String fileExtension;
    
    /**
     * Owner user ID
     */
    private Long ownerId;
    
    /**
     * Owner username
     */
    private String ownerName;
    
    /**
     * Document status (active, archived, deleted)
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
    
    /**
     * Indexed timestamp
     */
    private LocalDateTime indexedAt;
}