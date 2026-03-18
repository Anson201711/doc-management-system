package com.docman.document.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文档版本VO
 */
@Data
public class DocumentVersionVO {
    
    private Long id;
    
    private Long documentId;
    
    private Integer version;
    
    private String content;
    
    private String changeSummary;
    
    private Long creatorId;
    
    private String creatorName;
    
    private LocalDateTime createdAt;
}