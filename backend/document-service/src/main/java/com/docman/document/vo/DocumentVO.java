package com.docman.document.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文档VO
 */
@Data
public class DocumentVO {
    
    private Long id;
    
    private String title;
    
    private String content;
    
    private Long folderId;
    
    private Long creatorId;
    
    private String creatorName;
    
    private String status;
    
    private String documentType;
    
    private String[] tags;
    
    private Integer currentVersion;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}